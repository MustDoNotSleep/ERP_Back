package com.erp.controller;

import com.erp.dto.CourseApplicationDto;
import com.erp.dto.PageResponse;
import com.erp.service.CourseApplicationService;
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
@RequestMapping("/course-applications")
@RequiredArgsConstructor
public class CourseApplicationController {

    private final CourseApplicationService courseApplicationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<CourseApplicationDto.Response>>> getAllApplications(Pageable pageable) {
        Page<CourseApplicationDto.Response> applications = courseApplicationService.getAllApplications(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(applications)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<CourseApplicationDto.Response>>> getApplicationsByEmployeeId(
            @PathVariable Long employeeId) {
        List<CourseApplicationDto.Response> applications = courseApplicationService.getApplicationsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseApplicationDto.Response>> getApplicationById(@PathVariable Long id) {
        CourseApplicationDto.Response application = courseApplicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseApplicationDto.Response>> createApplication(
            @Valid @RequestBody CourseApplicationDto.Request request) {
        CourseApplicationDto.Response created = courseApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<CourseApplicationDto.Response>> approveOrRejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody CourseApplicationDto.ApprovalRequest request) {
        CourseApplicationDto.Response updated = courseApplicationService.approveOrReject(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        courseApplicationService.deleteApplication(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
