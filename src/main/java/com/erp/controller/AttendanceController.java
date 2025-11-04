package com.erp.controller;

import com.erp.dto.AttendanceDto;
import com.erp.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * 출근 처리
     */
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> checkIn(
        @RequestBody AttendanceDto.CheckInRequest request) {
        AttendanceDto.Response response = attendanceService.checkIn(request);
        return ResponseEntity.ok(ApiResponse.success("출근 처리되었습니다.", response));
    }
    
    /**
     * 퇴근 처리
     */
    @PostMapping("/check-out")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> checkOut(
        @RequestBody AttendanceDto.CheckOutRequest request) {
        AttendanceDto.Response response = attendanceService.checkOut(request);
        return ResponseEntity.ok(ApiResponse.success("퇴근 처리되었습니다.", response));
    }
    
    /**
     * 근태 등록 (관리자용)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> createAttendance(
        @RequestBody AttendanceDto.Request request) {
        AttendanceDto.Response response = attendanceService.createAttendance(request);
        return ResponseEntity.ok(ApiResponse.success("근태가 등록되었습니다.", response));
    }
    
    /**
     * 근태 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> getAttendance(
        @PathVariable Long id) {
        AttendanceDto.Response response = attendanceService.getAttendance(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 근태 수정 (관리자용)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<AttendanceDto.Response>> updateAttendance(
        @PathVariable Long id,
        @RequestBody AttendanceDto.Request request) {
        AttendanceDto.Response response = attendanceService.updateAttendance(id, request);
        return ResponseEntity.ok(ApiResponse.success("근태가 수정되었습니다.", response));
    }
    
    /**
     * 근태 삭제 (관리자용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(
        @PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("근태가 삭제되었습니다.", null));
    }
    
    /**
     * 특정 직원의 근태 조회
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getAttendancesByEmployee(
        @PathVariable Long employeeId) {
        List<AttendanceDto.Response> responses = attendanceService.getAttendancesByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 특정 직원의 기간별 근태 조회
     */
    @GetMapping("/employee/{employeeId}/period")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getAttendancesByEmployeeAndPeriod(
        @PathVariable Long employeeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto.Response> responses = attendanceService.getAttendancesByEmployeeAndPeriod(
            employeeId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 전체 기간별 근태 조회 (관리자용)
     */
    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> getAttendancesByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto.Response> responses = attendanceService.getAttendancesByPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 특정 직원의 근태 통계
     */
    @GetMapping("/employee/{employeeId}/statistics")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceDto.Statistics>> getAttendanceStatistics(
        @PathVariable Long employeeId,
        @RequestParam int year,
        @RequestParam int month) {
        AttendanceDto.Statistics statistics = attendanceService.getAttendanceStatistics(employeeId, year, month);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}
