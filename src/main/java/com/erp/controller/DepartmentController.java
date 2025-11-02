package com.erp.controller;

import com.erp.dto.DepartmentDto;
import com.erp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createDepartment(
        @RequestBody DepartmentDto.Request request) {
        Long departmentId = departmentService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Department created successfully", departmentId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DepartmentDto.Response>> getDepartment(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getDepartment(id)));
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getAllDepartments()));
    }
    
    /**
     * 중복 제거된 부서명 목록 (드롭다운용)
     * GET /api/departments/unique-names
     */
    @GetMapping("/unique-names")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getUniqueDepartmentNames() {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getUniqueDepartmentNames()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(
        @PathVariable Long id,
        @RequestBody DepartmentDto.UpdateRequest request) {
        departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
        @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
    }
}