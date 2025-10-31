package com.erp.controller;

import com.erp.dto.DocumentApplicationDto;
import com.erp.dto.PageResponse;
import com.erp.service.DocumentApplicationService;
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
@RequestMapping("/document-applications")
@RequiredArgsConstructor
public class DocumentApplicationController {

    private final DocumentApplicationService documentApplicationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<DocumentApplicationDto.Response>>> getAllApplications(Pageable pageable) {
        Page<DocumentApplicationDto.Response> applications = documentApplicationService.getAllApplications(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(applications)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<DocumentApplicationDto.Response>>> getApplicationsByEmployeeId(
            @PathVariable Long employeeId) {
        List<DocumentApplicationDto.Response> applications = documentApplicationService.getApplicationsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<DocumentApplicationDto.Response>> getApplicationById(@PathVariable Long id) {
        DocumentApplicationDto.Response application = documentApplicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DocumentApplicationDto.Response>> createApplication(
            @Valid @RequestBody DocumentApplicationDto.Request request) {
        DocumentApplicationDto.Response created = documentApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<DocumentApplicationDto.Response>> approveOrRejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody DocumentApplicationDto.ApprovalRequest request) {
        DocumentApplicationDto.Response updated = documentApplicationService.approveOrReject(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        documentApplicationService.deleteApplication(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
