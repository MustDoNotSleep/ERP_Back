package com.erp.service;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.Employee;
import com.erp.entity.enums.RequestStatus;
import com.erp.repository.AppointmentRequestRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {
    
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * 매일 새벽 1시에 실행되는 스케줄러
     * 승인된 발령 중 발령일자가 도래한 건들을 자동으로 적용
     */
    @Scheduled(cron = "0 0 1 * * ?") // 매일 01:00:00에 실행
    @Transactional
    public void applyDueAppointments() {
        log.info("=== 발령 자동 적용 스케줄러 시작 ===");
        
        LocalDate today = LocalDate.now();
        
        // 승인되었고, 발령일자가 오늘 또는 과거이며, 아직 적용되지 않은 발령 건들 조회
        List<AppointmentRequest> dueAppointments = appointmentRequestRepository
            .findAll()
            .stream()
            .filter(appointment -> 
                appointment.getStatus() == RequestStatus.APPROVED &&
                appointment.getEffectiveDate() != null &&
                !appointment.getEffectiveDate().isAfter(today) &&
                !appointment.getIsApplied()
            )
            .toList();
        
        log.info("적용 대상 발령 건 수: {}", dueAppointments.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (AppointmentRequest appointment : dueAppointments) {
            try {
                applyAppointment(appointment);
                successCount++;
                log.info("발령 적용 성공 - ID: {}, 대상 직원: {}, 발령일자: {}", 
                    appointment.getId(), 
                    appointment.getTargetEmployee().getName(), 
                    appointment.getEffectiveDate());
            } catch (Exception e) {
                failCount++;
                log.error("발령 적용 실패 - ID: {}, 오류: {}", appointment.getId(), e.getMessage(), e);
            }
        }
        
        log.info("=== 발령 자동 적용 스케줄러 종료 === (성공: {}, 실패: {})", successCount, failCount);
    }
    
    /**
     * 개별 발령을 적용하는 메서드
     */
    private void applyAppointment(AppointmentRequest appointment) {
        Employee targetEmployee = appointment.getTargetEmployee();
        
        // 새 부서가 지정된 경우 부서 변경 (전보)
        if (appointment.getNewDepartment() != null) {
            targetEmployee.assignToDepartment(appointment.getNewDepartment());
            log.debug("부서 변경: {} -> {}", 
                targetEmployee.getDepartment() != null ? targetEmployee.getDepartment().getDepartmentName() : "없음",
                appointment.getNewDepartment().getDepartmentName());
        }
        
        // 새 직급이 지정된 경우 직급 변경 (승진/강등)
        if (appointment.getNewPosition() != null) {
            targetEmployee.promoteToPosition(appointment.getNewPosition());
            log.debug("직급 변경: {} -> {}", 
                targetEmployee.getPosition() != null ? targetEmployee.getPosition().getPositionName() : "없음",
                appointment.getNewPosition().getPositionName());
        }
        
        // 직원 정보 저장
        employeeRepository.save(targetEmployee);
        
        // 발령 적용 완료 표시
        appointment.markAsApplied();
        appointmentRequestRepository.save(appointment);
        
        log.info("발령 적용 완료 - 직원: {}, 부서: {}, 직급: {}", 
            targetEmployee.getName(),
            targetEmployee.getDepartment() != null ? targetEmployee.getDepartment().getDepartmentName() : "없음",
            targetEmployee.getPosition() != null ? targetEmployee.getPosition().getPositionName() : "없음");
    }
    
    /**
     * 수동으로 특정 발령을 즉시 적용하는 메서드 (관리자용)
     */
    @Transactional
    public void applyAppointmentManually(Long appointmentId) {
        AppointmentRequest appointment = appointmentRequestRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("발령 건을 찾을 수 없습니다: " + appointmentId));
        
        if (appointment.getStatus() != RequestStatus.APPROVED) {
            throw new IllegalStateException("승인된 발령만 적용할 수 있습니다.");
        }
        
        if (appointment.getIsApplied()) {
            throw new IllegalStateException("이미 적용된 발령입니다.");
        }
        
        applyAppointment(appointment);
        log.info("수동 발령 적용 완료 - ID: {}", appointmentId);
    }
}
