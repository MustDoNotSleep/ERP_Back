package com.erp.controller;

import com.erp.dto.WorkExperienceDto;
import com.erp.service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/work-experiences")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<List<WorkExperienceDto.Response>>> getAllWorkExperiences() {
        List<WorkExperienceDto.Response> experiences = workExperienceService.getAllWorkExperiences();
        return ResponseEntity.ok(ApiResponse.success(experiences));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<WorkExperienceDto.Response>>> getWorkExperiencesByEmployeeId(
            @PathVariable Long employeeId) {
        List<WorkExperienceDto.Response> experiences = workExperienceService.getWorkExperiencesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(experiences));
    }
    
    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<WorkExperienceDto.Response>> createWorkExperience(
            @PathVariable Long employeeId,
            @Valid @RequestBody WorkExperienceDto.Request request) {
        WorkExperienceDto.Response created = workExperienceService.createWorkExperience(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<WorkExperienceDto.Response>> updateWorkExperience(
            @PathVariable Long id,
            @Valid @RequestBody WorkExperienceDto.UpdateRequest request) {
        WorkExperienceDto.Response updated = workExperienceService.updateWorkExperience(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteWorkExperience(@PathVariable Long id) {
        workExperienceService.deleteWorkExperience(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
