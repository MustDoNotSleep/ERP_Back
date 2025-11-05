package com.erp.service;

import com.erp.dto.AttendanceDto;
import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.entity.enums.AttendanceType;
import com.erp.repository.AttendanceRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

        /**
         * 오늘 출근 여부 확인 (프론트 토큰/세션용)
         */
        public boolean isCheckedInToday() {
            String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
            return attendanceRepository.findTodayAttendance(employee).isPresent();
        }
    
    // 근무 시간 기준 설정
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);  // 출근 시간 09:00
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);   // 퇴근 시간 18:00
    private static final int STANDARD_WORK_HOURS = 8;                     // 기본 근무시간 8시간
    private static final int LATE_THRESHOLD_MINUTES = 0;                   // 지각 기준 (출근시간 이후)
    
    /**
     * 출근 처리 - 시간에 따라 자동으로 근태 타입 결정
     */
    @Transactional
    public AttendanceDto.Response checkIn(AttendanceDto.CheckInRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        // 오늘 이미 출근했는지 확인
        attendanceRepository.findTodayAttendance(employee).ifPresent(a -> {
            throw new IllegalStateException("이미 출근 처리되었습니다.");
        });
        
        LocalDateTime checkInTime = LocalDateTime.now();
        AttendanceType attendanceType = determineCheckInType(checkInTime, request.getAttendanceType());
        
        Attendance attendance = Attendance.builder()
            .employee(employee)
            .checkIn(checkInTime)
            .attendanceType(attendanceType)
            .note(request.getNote())
            .build();
        
        Attendance saved = attendanceRepository.save(attendance);
        log.info("출근 처리 완료 - 직원: {}, 시간: {}, 타입: {}", 
            employee.getName(), saved.getCheckIn(), saved.getAttendanceType());
        
        return toResponse(saved);
    }
    
    /**
     * 퇴근 처리 - 시간에 따라 자동으로 근태 타입 결정
     */
    @Transactional
    public AttendanceDto.Response checkOut(AttendanceDto.CheckOutRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        Attendance attendance = attendanceRepository.findTodayAttendance(employee)
            .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));
        
        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("이미 퇴근 처리되었습니다.");
        }
        
        LocalDateTime checkOutTime = LocalDateTime.now();
        attendance.checkOut(checkOutTime);
        
        // 근무시간 기반으로 근태 타입 재설정
        AttendanceType finalType = determineFinalAttendanceType(
            attendance.getCheckIn(), checkOutTime, attendance.getAttendanceType());
        attendance.updateAttendanceType(finalType);
        
        // 노트 업데이트
        attendance.updateNote(request.getNote());
        
        // 변경사항 저장 (dirty checking으로 자동 저장됨)
        return toResponse(attendance);
    }
    
    /**
     * 출근 시 근태 타입 자동 결정
     */
    private AttendanceType determineCheckInType(LocalDateTime checkInTime, AttendanceType requestedType) {
        LocalTime checkInLocalTime = checkInTime.toLocalTime();
        
        // 재택근무, 주말근무, 휴일근무는 사용자가 직접 선택 (null이 아닌 경우만)
        if (requestedType != null && 
            (requestedType == AttendanceType.REMOTE || 
             requestedType == AttendanceType.WEEKEND_WORK || 
             requestedType == AttendanceType.HOLIDAY_WORK)) {
            return requestedType;
        }
        
        // 출근 시간 체크 - 09:00 이후면 지각
        if (checkInLocalTime.isAfter(WORK_START_TIME.plusMinutes(LATE_THRESHOLD_MINUTES))) {
            return AttendanceType.LATE;
        }
        
        return AttendanceType.NORMAL;
    }
    
    /**
     * 퇴근 시 최종 근태 타입 결정
     */
    private AttendanceType determineFinalAttendanceType(
        LocalDateTime checkIn, LocalDateTime checkOut, AttendanceType currentType) {
        
        LocalTime checkOutLocalTime = checkOut.toLocalTime();
        long workHours = java.time.Duration.between(checkIn, checkOut).toHours();
        
        // 재택근무, 주말근무, 휴일근무는 유지
        if (currentType == AttendanceType.REMOTE || 
            currentType == AttendanceType.WEEKEND_WORK || 
            currentType == AttendanceType.HOLIDAY_WORK) {
            return currentType;
        }
        
        // 조퇴 체크 - 18:00 이전 퇴근
        if (checkOutLocalTime.isBefore(WORK_END_TIME)) {
            log.debug("조퇴 판정 - 퇴근시간: {}, 기준시간: {}", checkOutLocalTime, WORK_END_TIME);
            return AttendanceType.EARLY_LEAVE;
        }
        
        // 야근 체크 - 20:00 (18시 + 2시간) 이후 퇴근
        if (checkOutLocalTime.isAfter(WORK_END_TIME.plusHours(2))) {
            log.debug("야근 판정 - 퇴근시간: {}, 근무시간: {}h", checkOutLocalTime, workHours);
            return AttendanceType.OVERTIME;
        }
        
        // 지각은 유지, 아니면 정상
        return currentType == AttendanceType.LATE ? AttendanceType.LATE : AttendanceType.NORMAL;
    }
    
    /**
     * 근태 등록 (관리자용)
     */
    @Transactional
    public AttendanceDto.Response createAttendance(AttendanceDto.Request request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        Attendance attendance = Attendance.builder()
            .employee(employee)
            .checkIn(request.getCheckIn())
            .checkOut(request.getCheckOut())
            .attendanceType(request.getAttendanceType())
            .note(request.getNote())
            .build();
        
        if (request.getCheckOut() != null) {
            attendance.checkOut(request.getCheckOut());
        }
        
        Attendance saved = attendanceRepository.save(attendance);
        
        return toResponse(saved);
    }
    
    /**
     * 근태 상세 조회
     */
    public AttendanceDto.Response getAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("근태 기록을 찾을 수 없습니다."));
        return toResponse(attendance);
    }
    
    /**
     * 근태 수정 (관리자용)
     */
    @Transactional
    public AttendanceDto.Response updateAttendance(Long id, AttendanceDto.Request request) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("근태 기록을 찾을 수 없습니다."));
        
        // 기존 레코드 삭제
        attendanceRepository.delete(attendance);
        attendanceRepository.flush(); // 즉시 삭제 반영
        
        // 출퇴근 시간 결정
        LocalDateTime newCheckIn = request.getCheckIn() != null ? request.getCheckIn() : attendance.getCheckIn();
        LocalDateTime newCheckOut = request.getCheckOut() != null ? request.getCheckOut() : attendance.getCheckOut();
        
        // 근태 타입 자동 결정
        AttendanceType newType = determineCheckInType(newCheckIn, null);
        if (newCheckOut != null) {
            newType = determineFinalAttendanceType(newCheckIn, newCheckOut, newType);
        }
        
        // 새로운 엔티티 생성
        Attendance newAttendance = Attendance.builder()
            .employee(attendance.getEmployee())
            .checkIn(newCheckIn)
            .checkOut(newCheckOut)
            .attendanceType(newType)
            .note(request.getNote() != null ? request.getNote() : attendance.getNote())
            .build();
        
        if (newCheckOut != null) {
            newAttendance.checkOut(newCheckOut);
        }
        
        Attendance saved = attendanceRepository.save(newAttendance);
        return toResponse(saved);
    }
    
    /**
     * 근태 삭제 (관리자용)
     */
    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("근태 기록을 찾을 수 없습니다."));
        attendanceRepository.delete(attendance);
    }
    
    /**
     * 특정 직원의 근태 조회
     */
    public List<AttendanceDto.Response> getAttendancesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        return attendanceRepository.findByEmployeeOrderByCheckInDesc(employee).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 직원의 기간별 근태 조회
     */
    public List<AttendanceDto.Response> getAttendancesByEmployeeAndPeriod(
        Long employeeId, LocalDate startDate, LocalDate endDate) {
        
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return attendanceRepository.findByEmployeeAndCheckInBetweenOrderByCheckInDesc(
            employee, startDateTime, endDateTime).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 전체 기간별 근태 조회 (관리자용)
     */
    public List<AttendanceDto.Response> getAttendancesByPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return attendanceRepository.findByCheckInBetweenOrderByCheckInDesc(
            startDateTime, endDateTime).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 직원의 근태 통계
     */
    public AttendanceDto.Statistics getAttendanceStatistics(Long employeeId, int year, int month) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<Attendance> attendances = attendanceRepository.findByEmployeeAndCheckInBetweenOrderByCheckInDesc(
            employee, startDateTime, endDateTime);
        
        Map<AttendanceType, Long> typeCounts = attendances.stream()
            .collect(Collectors.groupingBy(Attendance::getAttendanceType, Collectors.counting()));
        
        double totalWorkHours = attendances.stream()
            .mapToDouble(a -> a.getWorkHours() != null ? a.getWorkHours() : 0.0)
            .sum();
        
        double totalOvertimeHours = attendances.stream()
            .mapToDouble(a -> a.getOvertimeHours() != null ? a.getOvertimeHours() : 0.0)
            .sum();
        
        return AttendanceDto.Statistics.builder()
            .employeeId(employee.getId())
            .employeeName(employee.getName())
            .totalDays(attendances.size())
            .normalDays(typeCounts.getOrDefault(AttendanceType.NORMAL, 0L).intValue())
            .lateDays(typeCounts.getOrDefault(AttendanceType.LATE, 0L).intValue())
            .earlyLeaveDays(typeCounts.getOrDefault(AttendanceType.EARLY_LEAVE, 0L).intValue())
            .absentDays(typeCounts.getOrDefault(AttendanceType.ABSENT, 0L).intValue())
            .remoteDays(typeCounts.getOrDefault(AttendanceType.REMOTE, 0L).intValue())
            .overtimeDays(typeCounts.getOrDefault(AttendanceType.OVERTIME, 0L).intValue())
            .weekendWorkDays(typeCounts.getOrDefault(AttendanceType.WEEKEND_WORK, 0L).intValue())
            .holidayWorkDays(typeCounts.getOrDefault(AttendanceType.HOLIDAY_WORK, 0L).intValue())
            .totalWorkHours(totalWorkHours)
            .totalOvertimeHours(totalOvertimeHours)
            .build();
    }
    
    /**
     * 오늘 출퇴근 기록 반환 (checkIn, checkOut)
     */
    public AttendanceRecord getTodayAttendance() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        return attendanceRepository.findTodayAttendance(employee)
            .map(a -> new AttendanceRecord(a.getCheckIn(), a.getCheckOut()))
            .orElse(new AttendanceRecord(null, null));
    }

    public static class AttendanceRecord {
        public java.time.LocalDateTime checkInTime;
        public java.time.LocalDateTime checkOutTime;
        public AttendanceRecord(java.time.LocalDateTime checkInTime, java.time.LocalDateTime checkOutTime) {
            this.checkInTime = checkInTime;
            this.checkOutTime = checkOutTime;
        }
    }
    
    /**
     * Entity -> Response DTO 변환
     */
    private AttendanceDto.Response toResponse(Attendance attendance) {
        return AttendanceDto.Response.builder()
            .id(attendance.getId())
            .employeeId(attendance.getEmployee().getId())
            .employeeName(attendance.getEmployee().getName())
            .departmentName(attendance.getEmployee().getDepartment() != null ?
                attendance.getEmployee().getDepartment().getDepartmentName() : null)
            .checkIn(attendance.getCheckIn())
            .checkOut(attendance.getCheckOut())
            .attendanceType(attendance.getAttendanceType())
            .note(attendance.getNote())
            .workHours(attendance.getWorkHours())
            .overtimeHours(attendance.getOvertimeHours())
            .createdAt(attendance.getCreatedAt())
            .isOnLeave(attendance.isOnLeave())
            .leaveId(attendance.getLeave() != null ? attendance.getLeave().getId() : null)
            .leaveType(attendance.getLeave() != null ? attendance.getLeave().getType() : null)
            .leaveReason(attendance.getLeave() != null ? attendance.getLeave().getReason() : null)
            .build();
    }
}
