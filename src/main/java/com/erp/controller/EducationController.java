package com.erp.controller;

import com.erp.dto.EducationDto;
import com.erp.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<EducationDto.Response>>> getEducationsByEmployeeId(
            @PathVariable Long employeeId) {
        List<EducationDto.Response> educations = educationService.getEducationsByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(educations));
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<EducationDto.Response>> createEducation(
            @PathVariable Long employeeId,
            @Valid @RequestBody EducationDto.Request request) {
        EducationDto.Response created = educationService.createEducation(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<EducationDto.Response>> updateEducation(
            @PathVariable Long id,
            @Valid @RequestBody EducationDto.UpdateRequest request) {
        EducationDto.Response updated = educationService.updateEducation(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
