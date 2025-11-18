package com.erp.service;

import com.erp.dto.SeveranceDto;
import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeveranceService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;

    /**
     * 퇴직금 계산
     * 공식: (1일 평균임금 × 30일) × (재직일수 / 365)
     * - 1년 미만 근무자는 퇴직금 없음
     * - 1일 평균임금 = 최근 3개월 총 급여 / 90일
     */
    public SeveranceDto.CalculationResponse calculateSeverancePay(SeveranceDto.CalculationRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getEmployeeId().toString()));

        // 퇴사일 결정 (요청값 또는 실제 퇴사일 또는 오늘)
        LocalDate severanceDate = determineSeveranceDate(request.getSeveranceDate(), employee.getQuitDate());
        
        // 입사일 확인
        if (employee.getHireDate() == null) {
            throw new IllegalStateException("입사일 정보가 없습니다.");
        }

        // 재직일수 계산
        long totalWorkDays = ChronoUnit.DAYS.between(employee.getHireDate(), severanceDate);
        
        // 1년 미만 근무자 체크
        if (totalWorkDays < 365) {
            return buildZeroSeveranceResponse(employee, severanceDate, totalWorkDays, 
                "1년 미만 근무자는 퇴직금이 발생하지 않습니다.");
        }

        // 최근 3개월 급여 조회
        List<Salary> recentSalaries = getRecentSalaries(employee.getId(), severanceDate, 3);
        
        if (recentSalaries.isEmpty()) {
            return buildZeroSeveranceResponse(employee, severanceDate, totalWorkDays, 
                "최근 3개월 급여 데이터가 없습니다.");
        }

        // 평균임금 계산
        BigDecimal totalSalary = calculateTotalSalary(recentSalaries);
        BigDecimal last3MonthsAverage = totalSalary.divide(BigDecimal.valueOf(recentSalaries.size()), 2, RoundingMode.HALF_UP);
        
        // 1일 평균임금 = 최근 3개월 총 급여 / 90일
        BigDecimal averageDailyWage = totalSalary.divide(BigDecimal.valueOf(90), 2, RoundingMode.HALF_UP);
        
        // 퇴직금 = (1일 평균임금 × 30일) × (재직일수 / 365)
        BigDecimal monthlyAverage = averageDailyWage.multiply(BigDecimal.valueOf(30));
        BigDecimal severancePay = monthlyAverage
                .multiply(BigDecimal.valueOf(totalWorkDays))
                .divide(BigDecimal.valueOf(365), 0, RoundingMode.HALF_UP);

        // 예상 세금 (간이 계산: 퇴직소득세는 복잡하므로 참고용으로만)
        BigDecimal estimatedTax = calculateEstimatedTax(severancePay, totalWorkDays);
        BigDecimal netSeverancePay = severancePay.subtract(estimatedTax);

        long workYears = totalWorkDays / 365;
        
        String formula = String.format("(%s원 × 30일) × (%d일 / 365일) = %s원",
                averageDailyWage, totalWorkDays, severancePay);

        return SeveranceDto.CalculationResponse.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .hireDate(employee.getHireDate())
                .severanceDate(severanceDate)
                .totalWorkDays(totalWorkDays)
                .workYears(workYears)
                .averageDailyWage(averageDailyWage)
                .last3MonthsAverage(last3MonthsAverage)
                .monthlyBaseSalary(!recentSalaries.isEmpty() ? recentSalaries.get(0).getBaseSalary() : BigDecimal.ZERO)
                .severancePay(netSeverancePay)
                .severancePayBeforeTax(severancePay)
                .estimatedTax(estimatedTax)
                .calculationFormula(formula)
                .note("퇴직소득세는 근속년수, 공제액 등에 따라 달라지므로 실제 세액과 차이가 있을 수 있습니다.")
                .build();
    }

    /**
     * 퇴사일 결정 우선순위: 요청값 > 실제 퇴사일 > 오늘
     */
    private LocalDate determineSeveranceDate(LocalDate requestDate, LocalDate quitDate) {
        if (requestDate != null) {
            return requestDate;
        }
        if (quitDate != null) {
            return quitDate;
        }
        return LocalDate.now();
    }

    /**
     * 최근 N개월 급여 조회
     */
    private List<Salary> getRecentSalaries(Long employeeId, LocalDate baseDate, int months) {
        YearMonth endMonth = YearMonth.from(baseDate).minusMonths(1); // 전월까지
        YearMonth startMonth = endMonth.minusMonths(months - 1);
        
        return salaryRepository.findByEmployeeIdAndPaymentDateBetween(
                employeeId, startMonth, endMonth);
    }

    /**
     * 총 급여 계산 (기본급 + 보너스 + 야근수당 + 야간수당)
     */
    private BigDecimal calculateTotalSalary(List<Salary> salaries) {
        return salaries.stream()
                .map(salary -> {
                    BigDecimal total = salary.getBaseSalary();
                    if (salary.getBonus() != null) {
                        total = total.add(salary.getBonus());
                    }
                    if (salary.getOvertimeAllowance() != null) {
                        total = total.add(salary.getOvertimeAllowance());
                    }
                    if (salary.getNightAllowance() != null) {
                        total = total.add(salary.getNightAllowance());
                    }
                    return total;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 예상 퇴직소득세 간이 계산
     * 실제 퇴직소득세는 매우 복잡하므로 간략화된 계산식 사용
     * (참고용, 실제 세액과 다를 수 있음)
     */
    private BigDecimal calculateEstimatedTax(BigDecimal severancePay, long workDays) {
        // 근속년수
        int workYears = (int) (workDays / 365);
        
        // 근속년수 공제: 연 150만원 (5년 이하), 연 200만원 (5년 초과)
        BigDecimal deduction;
        if (workYears <= 5) {
            deduction = BigDecimal.valueOf(1500000L * workYears);
        } else {
            deduction = BigDecimal.valueOf(1500000L * 5 + 2000000L * (workYears - 5));
        }
        
        BigDecimal taxableAmount = severancePay.subtract(deduction);
        if (taxableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 간이세율 적용 (실제는 더 복잡)
        // 과세표준 구간별 세율 (매우 간략화)
        BigDecimal taxRate;
        if (taxableAmount.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
            taxRate = BigDecimal.valueOf(0.06); // 6%
        } else if (taxableAmount.compareTo(BigDecimal.valueOf(50000000)) <= 0) {
            taxRate = BigDecimal.valueOf(0.15); // 15%
        } else {
            taxRate = BigDecimal.valueOf(0.24); // 24%
        }
        
        return taxableAmount.multiply(taxRate).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 퇴직금 0원인 경우 응답 생성
     */
    private SeveranceDto.CalculationResponse buildZeroSeveranceResponse(
            Employee employee, LocalDate severanceDate, long totalWorkDays, String note) {
        
        return SeveranceDto.CalculationResponse.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .hireDate(employee.getHireDate())
                .severanceDate(severanceDate)
                .totalWorkDays(totalWorkDays)
                .workYears(totalWorkDays / 365)
                .averageDailyWage(BigDecimal.ZERO)
                .last3MonthsAverage(BigDecimal.ZERO)
                .monthlyBaseSalary(BigDecimal.ZERO)
                .severancePay(BigDecimal.ZERO)
                .severancePayBeforeTax(BigDecimal.ZERO)
                .estimatedTax(BigDecimal.ZERO)
                .calculationFormula("퇴직금 없음")
                .note(note)
                .build();
    }

    /**
     * 전체 직원 예상 퇴직금 계산 (오늘 기준)
     */
    public List<SeveranceDto.CalculationResponse> calculateAllSeverance() {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<SeveranceDto.CalculationResponse> results = new ArrayList<>();
        
        for (Employee employee : allEmployees) {
            // 입사일이 없는 경우 스킵
            if (employee.getHireDate() == null) {
                continue;
            }
            
            try {
                SeveranceDto.CalculationRequest request = SeveranceDto.CalculationRequest.builder()
                        .employeeId(employee.getId())
                        .build();
                
                SeveranceDto.CalculationResponse response = calculateSeverancePay(request);
                results.add(response);
            } catch (Exception e) {
                // 개별 직원 계산 실패 시 스킵 (로그는 추후 추가 가능)
                continue;
            }
        }
        
        return results;
    }

    /**
     * 퇴직자 목록 및 퇴직금 조회
     * 
     * @param year 퇴직 연도 (null이면 전체 퇴직자)
     * @return 퇴직자 목록 및 퇴직금
     */
    public List<SeveranceDto.CalculationResponse> calculateRetirementSeverance(Integer year) {
        List<Employee> employees;
        
        if (year != null) {
            // 특정 연도 퇴직자
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);
            employees = employeeRepository.findByQuitDateBetween(startDate, endDate);
        } else {
            // 전체 퇴직자 (quitDate가 있는 직원)
            employees = employeeRepository.findByQuitDateIsNotNull();
        }
        
        return employees.stream()
                .filter(employee -> employee.getHireDate() != null) // 입사일 있는 사람만
                .map(employee -> {
                    try {
                        SeveranceDto.CalculationRequest request = SeveranceDto.CalculationRequest.builder()
                                .employeeId(employee.getId())
                                .severanceDate(employee.getQuitDate()) // 실제 퇴사일 사용
                                .build();
                        
                        return calculateSeverancePay(request);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }
}
