package com.erp.service;

import com.erp.dto.AttendanceDto;
import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.exception.BusinessException;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.AttendanceRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 30);
    
    @Transactional
    public String checkIn(AttendanceDto.CheckInRequest request) {
        Employee employee = findEmployee(request.getEmployeeId());
        validateNoExistingCheckIn(employee);
        
        LocalDateTime checkInTime = request.getCheckInTime() != null ? 
            request.getCheckInTime() : LocalDateTime.now();
            
        Attendance.AttendanceType type = determineAttendanceType(checkInTime);
        
        Attendance attendance = Attendance.builder()
            .id(UUID.randomUUID().toString())
            .employee(employee)
            .checkIn(checkInTime)
            .type(type)
            .note(request.getNote())
            .build();
        
        attendanceRepository.save(attendance);
        return attendance.getId();
    }
    
    @Transactional
    public void checkOut(AttendanceDto.CheckOutRequest request) {
        Employee employee = findEmployee(request.getEmployeeId());
        Attendance attendance = attendanceRepository.findTodayAttendance(employee)
            .orElseThrow(() -> new BusinessException("No check-in record found", "NO_CHECK_IN"));
            
        LocalDateTime checkOutTime = request.getCheckOutTime() != null ? 
            request.getCheckOutTime() : LocalDateTime.now();
            
        attendance.checkOut(checkOutTime);
        
        if (request.getNote() != null) {
            attendance.setNote(request.getNote());
        }
    }
    
    public AttendanceDto.Response getAttendance(String id) {
        return attendanceRepository.findById(id)
            .map(AttendanceDto.Response::from)
            .orElseThrow(() -> new EntityNotFoundException("Attendance", id));
    }
    
    public List<AttendanceDto.Response> getEmployeeAttendance(
        String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        Employee employee = findEmployee(employeeId);
        return attendanceRepository.findByEmployeeAndCheckInBetween(
            employee, startDate, endDate
        ).stream()
            .map(AttendanceDto.Response::from)
            .collect(Collectors.toList());
    }
    
    public AttendanceDto.DailyStatusResponse getCurrentStatus(String employeeId) {
        Employee employee = findEmployee(employeeId);
        return attendanceRepository.findTodayAttendance(employee)
            .map(attendance -> AttendanceDto.DailyStatusResponse.builder()
                .employeeName(employee.getName())
                .departmentName(employee.getDepartment().getName())
                .status(attendance.getType())
                .lastCheckIn(attendance.getCheckIn())
                .currentWorkHours(calculateCurrentWorkHours(attendance))
                .build())
            .orElse(AttendanceDto.DailyStatusResponse.builder()
                .employeeName(employee.getName())
                .departmentName(employee.getDepartment().getName())
                .status(Attendance.AttendanceType.ABSENT)
                .build());
    }
    
    private Employee findEmployee(String id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id));
    }
    
    private void validateNoExistingCheckIn(Employee employee) {
        attendanceRepository.findTodayAttendance(employee).ifPresent(a -> {
            throw new BusinessException(
                "Employee already checked in today",
                "DUPLICATE_CHECK_IN"
            );
        });
    }
    
    private Attendance.AttendanceType determineAttendanceType(LocalDateTime checkInTime) {
        LocalTime time = checkInTime.toLocalTime();
        if (time.isAfter(LATE_THRESHOLD)) {
            return Attendance.AttendanceType.LATE;
        } else if (time.isAfter(WORK_START_TIME)) {
            return Attendance.AttendanceType.NORMAL;
        } else {
            return Attendance.AttendanceType.NORMAL;
        }
    }
    
    private Double calculateCurrentWorkHours(Attendance attendance) {
        if (attendance.getCheckOut() != null) {
            return attendance.getWorkHours();
        }
        
        LocalDateTime now = LocalDateTime.now();
        return (now.getHour() - attendance.getCheckIn().getHour()) 
            + (now.getMinute() - attendance.getCheckIn().getMinute()) / 60.0;
    }
}