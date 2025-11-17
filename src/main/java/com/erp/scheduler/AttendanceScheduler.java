package com.erp.scheduler;

import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.entity.enums.AttendanceType;
import com.erp.repository.AttendanceRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 근태 자동 처리 스케줄러
 * - 매일 특정 시간에 출근하지 않은 직원 자동 결근 처리
 * - 퇴근하지 않은 직원 자동 퇴근 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceScheduler {
    
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    
    // 출근 시작 시간 (09:00)
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    
    // 출근 마감 시간 (10:00) - 이 시간까지 출근 안 하면 결근
    private static final LocalTime CHECK_IN_DEADLINE = LocalTime.of(10, 0);
    
    // 퇴근 시간 (18:00)
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);
    
    // 자동 퇴근 처리 시간 (23:50) - 퇴근 안 찍은 직원 자동 처리
    private static final LocalTime AUTO_CHECKOUT_TIME = LocalTime.of(23, 50);
    
    /**
     * 매일 오전 10시 1분에 실행
     * 출근하지 않은 직원 자동 결근 처리
     */
    @Scheduled(cron = "0 1 10 * * MON-FRI") // 평일만 실행 (월~금)
    @Transactional
    public void autoMarkAbsent() {
        log.info("🔄 자동 결근 처리 스케줄러 시작");
        
        LocalDate today = LocalDate.now();
        
        // 주말이면 실행 안 함
        if (isWeekend(today)) {
            log.info("⏭️ 주말이므로 결근 처리를 건너뜁니다.");
            return;
        }
        
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        // 전체 직원 조회
        List<Employee> allEmployees = employeeRepository.findAll();
        
        int absentCount = 0;
        
        for (Employee employee : allEmployees) {
            // 오늘 출근 기록이 있는지 확인
            boolean hasCheckedIn = attendanceRepository.existsByEmployeeAndCheckInBetween(
                employee, todayStart, todayEnd
            );
            
            if (!hasCheckedIn) {
                // 출근 기록이 없으면 결근 처리
                Attendance absence = Attendance.builder()
                    .employee(employee)
                    .checkIn(today.atTime(CHECK_IN_DEADLINE)) // 마감 시간으로 기록
                    .checkOut(null)
                    .attendanceType(AttendanceType.ABSENT)
                    .note("자동 결근 처리 (출근 기록 없음)")
                    .workHours(0.0)
                    .overtimeHours(0.0)
                    .build();
                
                attendanceRepository.save(absence);
                absentCount++;
                
                log.info("❌ [결근] {} (ID: {}) - 출근 기록 없음", 
                    employee.getName(), employee.getId());
            }
        }
        
        log.info("✅ 자동 결근 처리 완료: {}명 결근 처리됨", absentCount);
    }
    
    /**
     * 매일 밤 11시 50분에 실행
     * 퇴근하지 않은 직원 자동 퇴근 처리
     */
    @Scheduled(cron = "0 50 23 * * MON-FRI") // 평일만 실행
    @Transactional
    public void autoCheckOut() {
        log.info("🔄 자동 퇴근 처리 스케줄러 시작");
        
        LocalDate today = LocalDate.now();
        
        // 주말이면 실행 안 함
        if (isWeekend(today)) {
            log.info("⏭️ 주말이므로 퇴근 처리를 건너뜁니다.");
            return;
        }
        
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        // 오늘 출근했지만 퇴근 안 한 기록 조회
        List<Attendance> attendances = attendanceRepository.findByCheckInBetweenOrderByCheckInDesc(
            todayStart, todayEnd
        );
        
        int autoCheckOutCount = 0;
        
        for (Attendance attendance : attendances) {
            // 퇴근 기록이 없고, 결근/휴가가 아닌 경우
            if (attendance.getCheckOut() == null && 
                attendance.getAttendanceType() != AttendanceType.ABSENT &&
                attendance.getAttendanceType() != AttendanceType.LEAVE) {
                
                // 자동 퇴근 처리
                LocalDateTime autoCheckOutTime = today.atTime(AUTO_CHECKOUT_TIME);
                attendance.updateCheckOut(autoCheckOutTime);
                
                // 근무 시간 계산
                long minutes = java.time.Duration.between(
                    attendance.getCheckIn(), autoCheckOutTime
                ).toMinutes();
                double hours = minutes / 60.0;
                
                attendance.updateWorkHours(hours);
                
                // 초과근무 계산 (8시간 초과 시)
                if (hours > 8.0) {
                    attendance.updateOvertimeHours(hours - 8.0);
                }
                
                attendanceRepository.save(attendance);
                autoCheckOutCount++;
                
                log.info("🏁 [자동퇴근] {} (ID: {}) - {}시간 근무", 
                    attendance.getEmployee().getName(), 
                    attendance.getEmployee().getId(),
                    String.format("%.1f", hours));
            }
        }
        
        log.info("✅ 자동 퇴근 처리 완료: {}명 자동 퇴근됨", autoCheckOutCount);
    }
    
    /**
     * 매시간 정각에 실행 (상태 체크용 - 선택사항)
     * 현재 근무 중인 직원 수 로깅
     */
    @Scheduled(cron = "0 0 * * * MON-FRI") // 평일 매시간 정각
    public void checkWorkingStatus() {
        LocalDate today = LocalDate.now();
        
        if (isWeekend(today)) {
            return;
        }
        
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        
        List<Attendance> todayAttendances = attendanceRepository.findByCheckInBetweenOrderByCheckInDesc(
            todayStart, todayEnd
        );
        
        long workingCount = todayAttendances.stream()
            .filter(a -> a.getCheckOut() == null)
            .filter(a -> a.getAttendanceType() == AttendanceType.NORMAL || 
                        a.getAttendanceType() == AttendanceType.LATE)
            .count();
        
        log.info("📊 [{}] 현재 근무 중: {}명 | 총 출근: {}명", 
            LocalTime.now().toString().substring(0, 5),
            workingCount, 
            todayAttendances.size());
    }
    
    /**
     * 주말 여부 확인
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
