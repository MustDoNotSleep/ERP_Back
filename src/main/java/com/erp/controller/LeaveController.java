// package com.erp.controller;

// import com.erp.dto.LeaveDto;
// import com.erp.service.LeaveService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/leaves")
// @RequiredArgsConstructor
// public class LeaveController {
    
//     private final LeaveService leaveService;
    
//     @PostMapping
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<ApiResponse<String>> requestLeave(
//         @RequestBody LeaveDto.Request request,
//         Authentication authentication) {
//         request.setEmployeeId(authentication.getName());
//         String leaveId = leaveService.requestLeave(request);
//         return ResponseEntity.ok(ApiResponse.success(
//             "Leave request submitted successfully", leaveId));
//     }
    
//     @GetMapping("/{id}")
//     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR') or " +
//                   "@leaveService.getLeave(#id).employeeName == authentication.principal.username")
//     public ResponseEntity<ApiResponse<LeaveDto.Response>> getLeave(
//         @PathVariable String id) {
//         return ResponseEntity.ok(ApiResponse.success(
//             leaveService.getLeave(id)));
//     }
    
//     @GetMapping("/employee/{employeeId}")
//     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR') or #employeeId == authentication.principal.id")
//     public ResponseEntity<ApiResponse<List<LeaveDto.Response>>> getEmployeeLeaves(
//         @PathVariable String employeeId) {
//         return ResponseEntity.ok(ApiResponse.success(
//             leaveService.getEmployeeLeaves(employeeId)));
//     }
    
//     @GetMapping("/pending")
//     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
//     public ResponseEntity<ApiResponse<List<LeaveDto.Response>>> getPendingLeaves() {
//         return ResponseEntity.ok(ApiResponse.success(
//             leaveService.getPendingLeaves()));
//     }
    
//     @PostMapping("/{id}/process")
//     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
//     public ResponseEntity<ApiResponse<Void>> processLeaveRequest(
//         @PathVariable String id,
//         @RequestBody LeaveDto.ApprovalRequest request,
//         Authentication authentication) {
//         request.setApproverId(authentication.getName());
//         leaveService.processLeaveRequest(id, request);
//         return ResponseEntity.ok(ApiResponse.success(
//             request.isApproved() ? "Leave request approved" : "Leave request rejected"));
//     }
    
//     @PostMapping("/{id}/cancel")
//     @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR') or " +
//                   "@leaveService.getLeave(#id).employeeName == authentication.principal.username")
//     public ResponseEntity<ApiResponse<Void>> cancelLeave(
//         @PathVariable String id) {
//         leaveService.cancelLeave(id);
//         return ResponseEntity.ok(ApiResponse.success("Leave request cancelled"));
//     }
// }