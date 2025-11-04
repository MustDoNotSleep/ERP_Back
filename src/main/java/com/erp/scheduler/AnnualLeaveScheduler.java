package com.erp.scheduler;

import com.erp.entity.Employee;
import com.erp.repository.EmployeeRepository;
import com.erp.service.AnnualLeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * 연차 자동 배급 스케줄러
 * - 매년 1월 1일 00:00: 1년 이상 근무자 연차 자동 발생
 * - 매월 1일 00:00: 1년 미만 근무자 월별 연차 발생
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnnualLeaveScheduler {
    
    private final EmployeeRepository employeeRepository;
    private final AnnualLeaveService annualLeaveService;
    
    /**
     * 매년 1월 1일 00:00 - 연차 자동 발생
     * Cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 1 1 *")
    @Transactional
    public void generateAnnualLeaveForAllEmployees() {
        log.info("=== 연차 자동 발생 시작 ===");
        
        int currentYear = LocalDate.now().getYear();
        List<Employee> employees = employeeRepository.findAll();
        
        log.info("총 직원 수: {}", employees.size());
        
        int generatedCount = 0;
        int skippedCount = 0;
        
        for (Employee employee : employees) {
            try {
                // 입사일 기준 근속년수 계산
                LocalDate hireDate = employee.getHireDate();
                int yearsOfService = Period.between(hireDate, LocalDate.of(currentYear, 1, 1)).getYears();
                
                log.info("직원 처리 중 - 이름: {}, 입사일: {}, 근속년수: {}년", 
                    employee.getName(), hireDate, yearsOfService);
                
                if (yearsOfService >= 1) {
                    // 1년 이상 근무자 연차 발생
                    var result = annualLeaveService.generateAnnualLeave(employee, currentYear);
                    if (result != null) {
                        generatedCount++;
                        log.info("✅ 연차 발생 완료 - 직원: {}, 근속: {}년, 발생일수: {}일", 
                            employee.getName(), yearsOfService, result.getTotalDays());
                    } else {
                        log.warn("⚠️ 연차 미발생 - 직원: {}, 근속: {}년 (출근율 미달 또는 이미 발생)", 
                            employee.getName(), yearsOfService);
                        skippedCount++;
                    }
                } else {
                    log.info("❌ 제외 - 직원: {}, 근속: {}년 (1년 미만)", employee.getName(), yearsOfService);
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("연차 발생 실패 - 직원: {}, 오류: {}", employee.getName(), e.getMessage(), e);
                skippedCount++;
            }
        }
        
        log.info("=== 연차 자동 발생 완료 - 발생: {}명, 제외: {}명 ===", generatedCount, skippedCount);
    }
    
    /**
     * 매월 1일 00:00 - 1년 미만 근무자 월별 연차 발생
     * Cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void generateMonthlyLeaveForNewEmployees() {
        log.info("=== 월별 연차 발생 시작 ===");
        
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        // 전월에 대한 연차 발생 (1일에 전월 만근 체크)
        int previousMonth = month == 1 ? 12 : month - 1;
        int previousYear = month == 1 ? year - 1 : year;
        
        List<Employee> employees = employeeRepository.findAll();
        
        int generatedCount = 0;
        int skippedCount = 0;
        
        for (Employee employee : employees) {
            try {
                LocalDate hireDate = employee.getHireDate();
                
                // 입사 1년 미만인지 확인
                if (Period.between(hireDate, now).getYears() < 1) {
                    annualLeaveService.generateMonthlyLeave(employee, previousYear, previousMonth);
                    generatedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("월별 연차 발생 실패 - 직원: {}, 오류: {}", employee.getName(), e.getMessage());
            }
        }
        
        log.info("=== 월별 연차 발생 완료 - 발생: {}명, 제외: {}명 ===", generatedCount, skippedCount);
    }
    
    /**
     * 신규 직원 입사 시 즉시 호출용 메서드
     */
    @Transactional
    public void generateLeaveForNewEmployee(Employee employee) {
        LocalDate hireDate = employee.getHireDate();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        
        int yearsOfService = Period.between(hireDate, now).getYears();
        
        if (yearsOfService >= 1) {
            // 1년 이상 근무자 - 연차 즉시 발생
            annualLeaveService.generateAnnualLeave(employee, currentYear);
            log.info("신규 직원 연차 발생 - 직원: {}, 근속: {}년", employee.getName(), yearsOfService);
        } else {
            // 1년 미만 근무자 - 초기 잔여 생성 (월별로 누적 예정)
            log.info("신규 직원 등록 - 직원: {}, 월별 연차 발생 대기", employee.getName());
        }
    }
}
