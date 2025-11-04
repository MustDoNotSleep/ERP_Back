package com.erp.controller;

import com.erp.dto.LeaveDto;
import com.erp.entity.enums.LeaveStatus;
import com.erp.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {
    
    private final LeaveService leaveService;
    
    /**
     * 휴가 신청
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<LeaveDto.Response>> requestLeave(
        @RequestBody LeaveDto.Request request) {
        LeaveDto.Response response = leaveService.requestLeave(request);
        return ResponseEntity.ok(ApiResponse.success("휴가 신청이 완료되었습니다.", response));
    }
    
    /**
     * 휴가 상세 조회
     */
    @GetMapping("/{leaveId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<LeaveDto.Response>> getLeave(@PathVariable Long leaveId) {
        LeaveDto.Response response = leaveService.getLeave(leaveId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 특정 직원의 휴가 목록 조회
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveDto.Response>>> getLeavesByEmployee(
        @PathVariable Long employeeId) {
        List<LeaveDto.Response> responses = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 대기 중인 휴가 목록 조회 (관리자용)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<List<LeaveDto.Response>>> getPendingLeaves() {
        List<LeaveDto.Response> responses = leaveService.getPendingLeaves();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 상태별 휴가 목록 조회 (관리자용)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<List<LeaveDto.Response>>> getLeavesByStatus(
        @PathVariable LeaveStatus status) {
        List<LeaveDto.Response> responses = leaveService.getLeavesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    
    /**
     * 휴가 승인/반려 처리
     */
    @PutMapping("/{leaveId}/process")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<LeaveDto.Response>> processLeave(
        @PathVariable Long leaveId,
        @RequestBody LeaveDto.ApprovalRequest request) {
        LeaveDto.Response response = leaveService.processLeave(leaveId, request);
        String message = request.getApproved() ? "휴가가 승인되었습니다." : "휴가가 반려되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }
    
    /**
     * 휴가 취소
     */
    @PutMapping("/{leaveId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<LeaveDto.Response>> cancelLeave(@PathVariable Long leaveId) {
        LeaveDto.Response response = leaveService.cancelLeave(leaveId);
        return ResponseEntity.ok(ApiResponse.success("휴가가 취소되었습니다.", response));
    }
    
    /**
     * 특정 직원의 연도별 휴가 통계
     */
    @GetMapping("/employee/{employeeId}/statistics")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<LeaveDto.Statistics>> getLeaveStatistics(
        @PathVariable Long employeeId,
        @RequestParam int year) {
        LeaveDto.Statistics statistics = leaveService.getLeaveStatistics(employeeId, year);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}