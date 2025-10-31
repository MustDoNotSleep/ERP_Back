package com.erp.controller;

import com.erp.dto.AppointmentRequestDto;
import com.erp.dto.PageResponse;
import com.erp.service.AppointmentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/appointment-requests")
@RequiredArgsConstructor
public class AppointmentRequestController {

    private final AppointmentRequestService appointmentRequestService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentRequestDto.Response>>> getAllRequests(Pageable pageable) {
        Page<AppointmentRequestDto.Response> requests = appointmentRequestService.getAllRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(requests)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<AppointmentRequestDto.Response>>> getRequestsByEmployeeId(
            @PathVariable Long employeeId) {
        List<AppointmentRequestDto.Response> requests = appointmentRequestService.getRequestsByTargetEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<AppointmentRequestDto.Response>> getRequestById(@PathVariable Long id) {
        AppointmentRequestDto.Response request = appointmentRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<AppointmentRequestDto.Response>> createRequest(
            @Valid @RequestBody AppointmentRequestDto.Request request) {
        AppointmentRequestDto.Response created = appointmentRequestService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<AppointmentRequestDto.Response>> approveOrRejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDto.ApprovalRequest request) {
        AppointmentRequestDto.Response updated = appointmentRequestService.approveOrReject(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        appointmentRequestService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
