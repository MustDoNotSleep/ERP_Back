package com.erp.controller;

import com.erp.dto.DepartmentDto;
import com.erp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> createDepartment(
        @RequestBody DepartmentDto.Request request) {
        String departmentId = departmentService.createDepartment(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Department created successfully", departmentId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DepartmentDto.Response>> getDepartment(
        @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getDepartment(id)));
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getAllDepartments()));
    }
    
    @GetMapping("/root")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getRootDepartments() {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getRootDepartments()));
    }
    
    @GetMapping("/{id}/children")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getChildDepartments(
        @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            departmentService.getChildDepartments(id)));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateDepartment(
        @PathVariable String id,
        @RequestBody DepartmentDto.UpdateRequest request) {
        departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
        @PathVariable String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
    }
}