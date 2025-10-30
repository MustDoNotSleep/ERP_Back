package com.erp.controller;

import com.erp.dto.EmployeeDto;
import com.erp.dto.PageResponse;
import com.erp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> registerEmployee(
        @RequestBody EmployeeDto.Request request) {
        String employeeId = employeeService.registerEmployee(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Employee registered successfully", employeeId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<EmployeeDto.Response>> getEmployee(
        @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
            employeeService.getEmployee(id)));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeDto.Response>>> getAllEmployees(
        Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
            employeeService.getAllEmployees(pageable)));
    }
    
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR')")
    public ResponseEntity<ApiResponse<Object>> getEmployeesByDepartment(
        @PathVariable String departmentId) {
        return ResponseEntity.ok(ApiResponse.success(
            employeeService.getEmployeesByDepartment(departmentId)));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(
        @PathVariable String id,
        @RequestBody EmployeeDto.UpdateRequest request) {
        employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully"));
    }
    
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
        @PathVariable String id,
        @RequestParam String newPassword) {
        employeeService.updatePassword(id, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
        @PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully"));
    }
}