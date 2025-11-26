package com.erp.service;

import com.erp.dto.LeaveDto;
import com.erp.entity.AnnualLeaveBalance;
import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.entity.Leave;
import com.erp.entity.enums.AttendanceType;
import com.erp.entity.enums.LeaveDuration;
import com.erp.entity.enums.LeaveStatus;
import com.erp.entity.enums.LeaveType;
import com.erp.repository.AnnualLeaveBalanceRepository;
import com.erp.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.repository.LeaveRepository;
import com.erp.repository.EmployeeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final AnnualLeaveBalanceRepository annualLeaveBalanceRepository;
    private final AnnualLeaveService annualLeaveService;
    private final AttendanceRepository attendanceRepository;
    
    /**
     * 전체 휴가 목록 조회 (페이징)
     */
    public Page<LeaveDto.Response> getAllLeaves(Pageable pageable) {
        return leaveRepository.findAllWithEmployee(pageable)
                .map(this::toResponse);
    }
    
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
        
        log.info("🔍 휴가 중복 체크 - 직원: {}, 기간: {} ~ {}, 기존 휴가 수: {}", 
            employee.getName(), request.getStartDate(), request.getEndDate(), overlappingLeaves.size());
        
        // 중복 체크: 같은 날짜에 오전/오후가 다르면 허용
        if (!overlappingLeaves.isEmpty()) {
            // 신청하려는 휴가가 반차/반반차인 경우
            if (request.getDuration().isHalfDay() || request.getDuration().isQuarterDay()) {
                log.info("⏰ 반차/반반차 신청 - 시간대: {}", request.getDuration().isAM() ? "오전" : "오후");
                
                // 같은 날짜에 같은 시간대(오전/오후) 휴가가 있는지 체크
                boolean hasConflict = overlappingLeaves.stream()
                    .anyMatch(existing -> {
                        // 종일 휴가가 있으면 무조건 충돌
                        if (existing.getDuration() == LeaveDuration.FULL_DAY) {
                            log.warn("❌ 충돌: 같은 날에 종일 휴가가 이미 있음 - {}", existing.getType().getKoreanName());
                            return true;
                        }
                        // 같은 시간대(AM/PM)인 경우만 충돌
                        boolean sameTimeSlot = (request.getDuration().isAM() && existing.getDuration().isAM()) ||
                                              (request.getDuration().isPM() && existing.getDuration().isPM());
                        if (sameTimeSlot) {
                            log.warn("❌ 충돌: 같은 시간대에 이미 휴가가 있음 - {} ({})", 
                                existing.getType().getKoreanName(), 
                                existing.getDuration().isAM() ? "오전" : "오후");
                        }
                        return sameTimeSlot;
                    });
                
                if (hasConflict) {
                    throw new IllegalStateException("해당 시간대에 이미 승인된 휴가가 있습니다.");
                } else {
                    log.info("✅ 중복 없음: 다른 시간대이므로 신청 가능");
                }
            } else {
                // 종일 휴가를 신청하는 경우 어떤 휴가든 있으면 충돌
                log.warn("❌ 충돌: 종일 휴가 신청인데 해당 기간에 이미 휴가가 있음");
                throw new IllegalStateException("해당 기간에 이미 승인된 휴가가 있습니다.");
            }
        }
        
        // 실제 사용 일수 계산
        double leaveDays = calculateLeaveDays(request.getStartDate(), request.getEndDate(), request.getDuration());
        
        log.info("휴가 일수 계산 - 시작일: {}, 종료일: {}, duration: {}, 계산된 일수: {}일",
            request.getStartDate(), request.getEndDate(), request.getDuration().getKoreanName(), leaveDays);
        
        // 휴가 종류별 법정 일수 제한 검증
        if (!request.getType().isValidDays(leaveDays)) {
            throw new IllegalStateException(
                String.format("%s는 %s만 신청 가능합니다. (신청: %.1f일)",
                    request.getType().getKoreanName(),
                    request.getType().getDaysRangeDescription(),
                    leaveDays));
        }
        
        // 연차인 경우 잔여 연차 확인
        if (request.getType().isDeductFromAnnual()) {
            int year = request.getStartDate().getYear();
            AnnualLeaveBalance balance = annualLeaveBalanceRepository
                .findByEmployeeAndYear(employee, year)
                .orElseThrow(() -> new IllegalStateException("사용 가능한 연차가 없습니다."));
            
            if (balance.getRemainingDays() < leaveDays) {
                throw new IllegalStateException(
                    String.format("연차가 부족합니다. (신청: %.1f일, 잔여: %.1f일)", 
                        leaveDays, balance.getRemainingDays()));
            }
        }
        
        Leave leave = Leave.builder()
            .employee(employee)
            .type(request.getType())
            .duration(request.getDuration())
            .leaveDays(leaveDays)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .reason(request.getReason())
            .status(LeaveStatus.PENDING)
            .build();
        
        Leave saved = leaveRepository.save(leave);
        
        log.info("휴가 신청 완료 - 직원: {}, 종류: {} ({}), 기간: {} ~ {}, 일수: {}일",
            employee.getName(), 
            saved.getType().getKoreanName(),
            saved.getType().isPaid() ? "유급" : "무급",
            saved.getStartDate(), 
            saved.getEndDate(),
            saved.getLeaveDays());
        
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
            log.info("🔍 휴가 승인 시작 - 휴가 ID: {}, 직원: {}, 종류: {}, 일수: {}일", 
                leaveId, leave.getEmployee().getName(), leave.getType().getKoreanName(), leave.getLeaveDays());
            
            // 승인 - 연차 차감 및 참조 설정
            if (leave.getType().isDeductFromAnnual()) {
                log.info("✅ 연차 차감 대상 확인 - isDeductFromAnnual: true");
                
                int year = leave.getStartDate().getYear();
                AnnualLeaveBalance balance = annualLeaveBalanceRepository
                    .findByEmployeeAndYear(leave.getEmployee(), year)
                    .orElseThrow(() -> new IllegalStateException("연차 잔여 정보를 찾을 수 없습니다."));
                
                log.info("📊 차감 전 - 총: {}일, 사용: {}일, 잔여: {}일", 
                    balance.getTotalDays(), balance.getUsedDays(), balance.getRemainingDays());
                
                balance.useLeave(leave.getLeaveDays());
                leave.setAnnualLeaveBalance(balance); // 연차 잔여 참조 설정
                
                log.info("📊 차감 후 - 총: {}일, 사용: {}일, 잔여: {}일", 
                    balance.getTotalDays(), balance.getUsedDays(), balance.getRemainingDays());
                log.info("✅ 연차 차감 완료 - 직원: {}, 차감: {}일, 최종 잔여: {}일",
                    leave.getEmployee().getName(), leave.getLeaveDays(), balance.getRemainingDays());
            } else {
                log.info("⏭️ 연차 차감 스킵 - isDeductFromAnnual: false (종류: {})", leave.getType().getKoreanName());
            }
            
            leave.approve(approver);
            
            // 휴가 기간 동안 출근 기록 자동 생성
            createAttendanceRecordsForLeave(leave);
            
            log.info("휴가 승인 완료 - 휴가 ID: {}, 승인자: {}, 종류: {} ({})",
                leaveId, approver.getName(), 
                leave.getType().getKoreanName(),
                leave.getType().isPaid() ? "유급" : "무급");
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
        
        // 승인된 연차였다면 복구
        if (leave.getStatus() == LeaveStatus.APPROVED && leave.getType().isDeductFromAnnual()) {
            int year = leave.getStartDate().getYear();
            AnnualLeaveBalance balance = annualLeaveBalanceRepository
                .findByEmployeeAndYear(leave.getEmployee(), year)
                .orElseThrow(() -> new IllegalStateException("연차 잔여 정보를 찾을 수 없습니다."));
            
            balance.cancelLeave(leave.getLeaveDays());
            log.info("연차 복구 - 직원: {}, 복구: {}일, 잔여: {}일",
                leave.getEmployee().getName(), leave.getLeaveDays(), balance.getRemainingDays());
        }
        
        // 승인된 휴가였다면 생성된 출근 기록 삭제
        if (leave.getStatus() == LeaveStatus.APPROVED) {
            deleteAttendanceRecordsForLeave(leave);
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
        
        // 연차 잔여 정보 조회
        AnnualLeaveBalance balance = annualLeaveBalanceRepository
            .findByEmployeeAndYear(employee, year)
            .orElse(null);
        
        double totalAnnualLeave = balance != null ? balance.getTotalDays() : 0.0;
        double usedAnnualLeave = balance != null ? balance.getUsedDays() : 0.0;
        double remainingAnnualLeave = balance != null ? balance.getRemainingDays() : 0.0;
        
        // 기타 휴가 통계
        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesByEmployeeAndYear(employee, year);
        
        long sickDays = approvedLeaves.stream()
            .filter(l -> l.getType().getKoreanName().contains("병가"))
            .mapToLong(l -> l.getLeaveDays().longValue())
            .sum();
        
        long maternityDays = approvedLeaves.stream()
            .filter(l -> l.getType().getKoreanName().contains("출산") || 
                        l.getType().getKoreanName().contains("육아"))
            .mapToLong(this::calculateTotalDays)
            .sum();
        
        long bereavementDays = approvedLeaves.stream()
            .filter(l -> l.getType().getKoreanName().contains("경조사") ||
                        l.getType().getKoreanName().contains("결혼"))
            .mapToLong(l -> l.getLeaveDays().longValue())
            .sum();
        
        return LeaveDto.Statistics.builder()
            .employeeId(employee.getId())
            .employeeName(employee.getName())
            .year(year)
            .totalAnnualLeave(totalAnnualLeave)
            .usedAnnualLeave(usedAnnualLeave)
            .remainingAnnualLeave(remainingAnnualLeave)
            .totalSickLeave((int) sickDays)
            .totalMaternityLeave((int) maternityDays)
            .totalBereavementLeave((int) bereavementDays)
            .build();
    }
    
    /**
     * 휴가 승인 시 출근 기록 자동 생성
     */
    private void createAttendanceRecordsForLeave(Leave leave) {
        LocalDate currentDate = leave.getStartDate();
        LocalDate endDate = leave.getEndDate();
        
        while (!currentDate.isAfter(endDate)) {
            // 주말 제외 (토요일=6, 일요일=7)
            if (currentDate.getDayOfWeek().getValue() < 6) {
                // 이미 출근 기록이 있는지 확인
                LocalDateTime dayStart = currentDate.atStartOfDay();
                LocalDateTime dayEnd = currentDate.atTime(23, 59, 59);
                
                boolean hasAttendance = attendanceRepository.existsByEmployeeAndCheckInBetween(
                    leave.getEmployee(), dayStart, dayEnd);
                
                if (!hasAttendance) {
                    // 휴가 출근 기록 생성
                    Attendance attendance = Attendance.builder()
                        .employee(leave.getEmployee())
                        .checkIn(currentDate.atTime(9, 0)) // 09:00으로 설정
                        .checkOut(currentDate.atTime(18, 0)) // 18:00으로 설정
                        .attendanceType(AttendanceType.LEAVE)
                        .leave(leave)
                        .note(leave.getType().getKoreanName() + " - " + leave.getReason())
                        .workHours(0.0)
                        .overtimeHours(0.0)
                        .build();
                    
                    attendanceRepository.save(attendance);
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        log.info("휴가 출근 기록 생성 완료 - 직원: {}, 기간: {} ~ {}",
            leave.getEmployee().getName(), leave.getStartDate(), leave.getEndDate());
    }
    
    /**
     * 휴가 취소 시 출근 기록 삭제
     */
    private void deleteAttendanceRecordsForLeave(Leave leave) {
        LocalDate currentDate = leave.getStartDate();
        LocalDate endDate = leave.getEndDate();
        int deletedCount = 0;
        
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.atTime(23, 59, 59);
            
            // 휴가로 인한 출근 기록 조회 및 삭제
            List<Attendance> attendances = attendanceRepository
                .findByEmployeeAndCheckInBetweenOrderByCheckInDesc(leave.getEmployee(), dayStart, dayEnd);
            
            for (Attendance attendance : attendances) {
                if (attendance.isOnLeave() && 
                    attendance.getLeave() != null && 
                    attendance.getLeave().getId().equals(leave.getId())) {
                    attendanceRepository.delete(attendance);
                    deletedCount++;
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        log.info("휴가 출근 기록 삭제 완료 - 직원: {}, 삭제 건수: {}",
            leave.getEmployee().getName(), deletedCount);
    }
    
    /**
     * 휴가 일수 계산 (duration 반영)
     */
    /**
     * 휴가 일수 계산
     * - FULL_DAY: 날짜 차이만큼 (예: 3일 연차 = 3.0일)
     * - HALF_DAY_AM/PM: 0.5일 (오전/오후 반차)
     * - QUARTER_DAY_AM/PM: 0.25일 (오전/오후 반반차)
     */
    private double calculateLeaveDays(LocalDate startDate, LocalDate endDate, LeaveDuration duration) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // duration에 따라 실제 사용 일수 계산
        if (duration.isHalfDay()) {
            return 0.5;  // 오전/오후 반차 모두 0.5일
        } else if (duration.isQuarterDay()) {
            return 0.25; // 오전/오후 반반차 모두 0.25일
        } else {
            // FULL_DAY인 경우 날짜 차이만큼 (주말 포함)
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
            // 휴가 종류별 법정 일수 정보 추가
            .minDays(leave.getType().getMinDays())
            .maxDays(leave.getType().getMaxDays())
            .daysRangeDescription(leave.getType().getDaysRangeDescription())
            .build();
    }
}
