package com.erp.service;

import com.erp.entity.AnnualLeaveBalance;
import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.entity.enums.AttendanceType;
import com.erp.repository.AnnualLeaveBalanceRepository;
import com.erp.repository.AttendanceRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 연차 계산 및 관리 서비스
 * - 근로기준법 기준 연차 계산
 * - 1년 미만: 월 1일 (만근 시)
 * - 1년 이상: 연 15일 (출근율 80% 이상)
 * - 3년 이상: 2년마다 1일씩 추가 (최대 25일)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnnualLeaveService {
    
    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    
    private static final double ATTENDANCE_RATE_THRESHOLD = 0.8; // 출근율 80%
    private static final int BASE_ANNUAL_LEAVE = 15; // 기본 연차 15일
    private static final int MAX_ANNUAL_LEAVE = 25; // 최대 연차 25일
    
    /**
     * 특정 연도의 연차 발생 (1년 이상 근무자)
     */
    @Transactional
    public AnnualLeaveBalance generateAnnualLeave(Employee employee, int year) {
        // 이미 생성된 연차가 있는지 확인
        return annualLeaveBalanceRepository.findByEmployeeAndYear(employee, year)
            .orElseGet(() -> {
                LocalDate hireDate = employee.getHireDate();
                LocalDate yearStart = LocalDate.of(year, 1, 1);
                
                // 근속 년수 계산
                int yearsOfService = Period.between(hireDate, yearStart).getYears();
                
                if (yearsOfService < 1) {
                    // 1년 미만은 월별로 발생하므로 여기서 처리하지 않음
                    return null;
                }
                
                // 전년도 출근율 확인
                double attendanceRate = calculateAttendanceRate(employee, year - 1);
                
                if (attendanceRate < ATTENDANCE_RATE_THRESHOLD) {
                    log.warn("출근율 미달로 연차 미발생 - 직원: {}, 연도: {}, 출근율: {}%", 
                        employee.getName(), year, attendanceRate * 100);
                    return null;
                }
                
                // 연차 일수 계산
                double annualLeaveDays = calculateAnnualLeaveDays(yearsOfService);
                
                // 만료일 설정 (발생일로부터 2년)
                LocalDate expiryDate = yearStart.plusYears(2).minusDays(1);
                
                AnnualLeaveBalance balance = AnnualLeaveBalance.builder()
                    .employee(employee)
                    .year(year)
                    .totalDays(annualLeaveDays)
                    .usedDays(0.0)
                    .remainingDays(annualLeaveDays)
                    .expiryDate(expiryDate)
                    .note(String.format("%d년차 연차 (%d일)", yearsOfService, (int)annualLeaveDays))
                    .build();
                
                log.info("연차 발생 - 직원: {}, 연도: {}, 발생일수: {}일", 
                    employee.getName(), year, annualLeaveDays);
                
                return annualLeaveBalanceRepository.save(balance);
            });
    }
    
    /**
     * 월별 연차 발생 (1년 미만 근무자)
     */
    @Transactional
    public void generateMonthlyLeave(Employee employee, int year, int month) {
        LocalDate hireDate = employee.getHireDate();
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        
        // 입사 1년 이상이면 처리하지 않음
        if (ChronoUnit.MONTHS.between(hireDate, monthStart) >= 12) {
            return;
        }
        
        // 해당 월 만근 확인
        boolean isPerfectAttendance = checkPerfectAttendance(employee, monthStart, monthEnd);
        
        if (!isPerfectAttendance) {
            log.info("만근 아님 - 직원: {}, 연월: {}-{}", employee.getName(), year, month);
            return;
        }
        
        // 연차 잔여에 1일 추가
        AnnualLeaveBalance balance = annualLeaveBalanceRepository
            .findByEmployeeAndYear(employee, year)
            .orElseGet(() -> {
                AnnualLeaveBalance newBalance = AnnualLeaveBalance.builder()
                    .employee(employee)
                    .year(year)
                    .totalDays(0.0)
                    .usedDays(0.0)
                    .remainingDays(0.0)
                    .expiryDate(LocalDate.of(year + 2, 12, 31))
                    .note("1년 미만 월별 발생")
                    .build();
                return annualLeaveBalanceRepository.save(newBalance);
            });
        
        balance.addLeave(1.0, String.format("%d월 만근", month));
        log.info("월별 연차 발생 - 직원: {}, 연월: {}-{}, 추가: 1일", 
            employee.getName(), year, month);
    }
    
    /**
     * 연차 일수 계산
     */
    private double calculateAnnualLeaveDays(int yearsOfService) {
        if (yearsOfService < 1) {
            return 0;
        }
        
        // 기본 15일
        double days = BASE_ANNUAL_LEAVE;
        
        // 3년 이상부터 2년마다 1일 추가
        if (yearsOfService >= 3) {
            int additionalYears = yearsOfService - 1; // 2년차까지는 15일
            int bonusDays = additionalYears / 2;
            days += bonusDays;
        }
        
        // 최대 25일 제한
        return Math.min(days, MAX_ANNUAL_LEAVE);
    }
    
    /**
     * 출근율 계산
     */
    private double calculateAttendanceRate(Employee employee, int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        
        List<Attendance> attendances = attendanceRepository
            .findByEmployeeAndCheckInBetweenOrderByCheckInDesc(
                employee, 
                yearStart.atStartOfDay(), 
                yearEnd.atTime(23, 59, 59)
            );
        
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        // 총 근무일수 (주말 제외한 평일 수)
        long totalWorkDays = countWeekdays(yearStart, yearEnd);
        
        // 정상 출근일수 (결근 제외)
        long normalAttendanceDays = attendances.stream()
            .filter(a -> a.getAttendanceType() != AttendanceType.ABSENT)
            .count();
        
        return (double) normalAttendanceDays / totalWorkDays;
    }
    
    /**
     * 만근 여부 확인
     */
    private boolean checkPerfectAttendance(Employee employee, LocalDate start, LocalDate end) {
        List<Attendance> attendances = attendanceRepository
            .findByEmployeeAndCheckInBetweenOrderByCheckInDesc(
                employee,
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
            );
        
        // 평일 수 계산
        long weekdays = countWeekdays(start, end);
        
        // 결근이 없고, 평일 수만큼 출근했는지 확인
        long normalDays = attendances.stream()
            .filter(a -> a.getAttendanceType() != AttendanceType.ABSENT)
            .count();
        
        return normalDays >= weekdays;
    }
    
    /**
     * 평일 수 계산 (주말 제외)
     */
    private long countWeekdays(LocalDate start, LocalDate end) {
        long days = 0;
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek().getValue() < 6) { // 월~금 (1~5)
                days++;
            }
            current = current.plusDays(1);
        }
        
        return days;
    }
    
    /**
     * 연차 잔여 조회
     */
    public AnnualLeaveBalance getBalance(Employee employee, int year) {
        return annualLeaveBalanceRepository.findByEmployeeAndYear(employee, year)
            .orElse(null);
    }
}
