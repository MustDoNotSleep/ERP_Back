package com.erp.service;

import com.erp.dto.LeaveDto;
import com.erp.entity.Employee;
import com.erp.entity.Leave;
import com.erp.entity.enums.LeaveDuration;
import com.erp.entity.enums.LeaveStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.repository.LeaveRepository;
import com.erp.repository.EmployeeRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaveService {
    
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * 휴가 신청
     */
    @Transactional
    public LeaveDto.Response requestLeave(LeaveDto.Request request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        // 휴가 기간 중복 체크
        List<Leave> overlappingLeaves = leaveRepository.findApprovedLeavesInPeriod(
            employee, request.getStartDate(), request.getEndDate());
        
        if (!overlappingLeaves.isEmpty()) {
            throw new IllegalStateException("해당 기간에 이미 승인된 휴가가 있습니다.");
        }
        
        Leave leave = Leave.builder()
            .employee(employee)
            .type(request.getType())
            .duration(request.getDuration())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .reason(request.getReason())
            .status(LeaveStatus.PENDING)
            .build();
        
        Leave saved = leaveRepository.save(leave);
        log.info("휴가 신청 완료 - 직원: {}, 종류: {}, 기간: {} ~ {}",
            employee.getName(), saved.getType(), saved.getStartDate(), saved.getEndDate());
        
        return toResponse(saved);
    }
    
    /**
     * 휴가 상세 조회
     */
    public LeaveDto.Response getLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new IllegalArgumentException("휴가를 찾을 수 없습니다."));
        
        return toResponse(leave);
    }
    
    /**
     * 특정 직원의 휴가 목록 조회
     */
    public List<LeaveDto.Response> getLeavesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        return leaveRepository.findByEmployeeOrderByStartDateDesc(employee).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 대기 중인 휴가 목록 조회 (관리자용)
     */
    public List<LeaveDto.Response> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByStartDateDesc(LeaveStatus.PENDING).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 상태별 휴가 목록 조회 (관리자용)
     */
    public List<LeaveDto.Response> getLeavesByStatus(LeaveStatus status) {
        return leaveRepository.findByStatusOrderByStartDateDesc(status).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 휴가 승인/반려 처리
     */
    @Transactional
    public LeaveDto.Response processLeave(Long leaveId, LeaveDto.ApprovalRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee approver = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("승인자를 찾을 수 없습니다."));
        
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new IllegalArgumentException("휴가를 찾을 수 없습니다."));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("대기 중인 휴가만 처리할 수 있습니다.");
        }
        
        if (request.getApproved()) {
            leave.approve(approver);
            log.info("휴가 승인 완료 - 휴가 ID: {}, 승인자: {}", leaveId, approver.getName());
        } else {
            leave.reject(approver);
            log.info("휴가 반려 완료 - 휴가 ID: {}, 반려자: {}", leaveId, approver.getName());
        }
        
        Leave saved = leaveRepository.save(leave);
        return toResponse(saved);
    }
    
    /**
     * 휴가 취소
     */
    @Transactional
    public LeaveDto.Response cancelLeave(Long leaveId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new IllegalArgumentException("휴가를 찾을 수 없습니다."));
        
        // 본인의 휴가만 취소 가능
        if (!leave.getEmployee().getId().equals(employee.getId())) {
            throw new IllegalStateException("본인의 휴가만 취소할 수 있습니다.");
        }
        
        if (leave.getStatus() == LeaveStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 휴가입니다.");
        }
        
        leave.cancel();
        Leave saved = leaveRepository.save(leave);
        log.info("휴가 취소 완료 - 휴가 ID: {}, 직원: {}", leaveId, employee.getName());
        
        return toResponse(saved);
    }
    
    /**
     * 특정 직원의 연도별 휴가 통계
     */
    public LeaveDto.Statistics getLeaveStatistics(Long employeeId, int year) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesByEmployeeAndYear(employee, year);
        
        double usedAnnualLeave = approvedLeaves.stream()
            .filter(l -> l.getType() == com.erp.entity.enums.LeaveType.ANNUAL)
            .mapToDouble(this::calculateLeaveDays)
            .sum();
        
        long sickDays = approvedLeaves.stream()
            .filter(l -> l.getType() == com.erp.entity.enums.LeaveType.SICK)
            .count();
        
        long maternityDays = approvedLeaves.stream()
            .filter(l -> l.getType() == com.erp.entity.enums.LeaveType.MATERNITY)
            .mapToLong(this::calculateTotalDays)
            .sum();
        
        long bereavementDays = approvedLeaves.stream()
            .filter(l -> l.getType() == com.erp.entity.enums.LeaveType.BEREAVEMENT)
            .count();
        
        // 연차는 보통 15일 기본 (입사년수에 따라 다를 수 있음)
        double totalAnnualLeave = 15.0;
        
        return LeaveDto.Statistics.builder()
            .employeeId(employee.getId())
            .employeeName(employee.getName())
            .year(year)
            .totalAnnualLeave(totalAnnualLeave)
            .usedAnnualLeave(usedAnnualLeave)
            .remainingAnnualLeave(totalAnnualLeave - usedAnnualLeave)
            .totalSickLeave((int) sickDays)
            .totalMaternityLeave((int) maternityDays)
            .totalBereavementLeave((int) bereavementDays)
            .build();
    }
    
    /**
     * 휴가 일수 계산 (duration 반영)
     */
    private double calculateLeaveDays(Leave leave) {
        long daysBetween = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        
        // duration에 따라 실제 사용 일수 계산
        if (leave.getDuration() == LeaveDuration.HALF_DAY) {
            return 0.5;
        } else if (leave.getDuration() == LeaveDuration.QUARTER_DAY) {
            return 0.25;
        } else {
            return daysBetween;
        }
    }
    
    /**
     * 총 일수 계산
     */
    private long calculateTotalDays(Leave leave) {
        return ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
    }
    
    /**
     * Entity -> Response DTO 변환
     */
    private LeaveDto.Response toResponse(Leave leave) {
        return LeaveDto.Response.builder()
            .id(leave.getId())
            .employeeId(leave.getEmployee().getId())
            .employeeName(leave.getEmployee().getName())
            .departmentName(leave.getEmployee().getDepartment() != null ?
                leave.getEmployee().getDepartment().getDepartmentName() : null)
            .type(leave.getType())
            .duration(leave.getDuration())
            .startDate(leave.getStartDate())
            .endDate(leave.getEndDate())
            .reason(leave.getReason())
            .status(leave.getStatus())
            .approvedById(leave.getApprovedBy() != null ? leave.getApprovedBy().getId() : null)
            .approvedByName(leave.getApprovedBy() != null ? leave.getApprovedBy().getName() : null)
            .approvedAt(leave.getApprovedAt())
            .createdAt(leave.getCreatedAt() != null ? leave.getCreatedAt().toLocalDate() : null)
            .build();
    }
}
