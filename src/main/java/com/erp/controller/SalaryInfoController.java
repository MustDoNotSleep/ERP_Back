package com.erp.controller;

import com.erp.dto.SalaryInfoDto;
import com.erp.service.SalaryInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/salary-info")
@RequiredArgsConstructor
public class SalaryInfoController {

    private final SalaryInfoService salaryInfoService;

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<SalaryInfoDto.Response>> getSalaryInfoByEmployeeId(
            @PathVariable Long employeeId) {
        Optional<SalaryInfoDto.Response> salaryInfo = salaryInfoService.getSalaryInfoByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(salaryInfo.orElse(null)));
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<SalaryInfoDto.Response>> createSalaryInfo(
            @PathVariable Long employeeId,
            @Valid @RequestBody SalaryInfoDto.Request request) {
        SalaryInfoDto.Response created = salaryInfoService.createSalaryInfo(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<SalaryInfoDto.Response>> updateSalaryInfo(
            @PathVariable Long id,
            @Valid @RequestBody SalaryInfoDto.UpdateRequest request) {
        SalaryInfoDto.Response updated = salaryInfoService.updateSalaryInfo(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryInfo(@PathVariable Long id) {
        salaryInfoService.deleteSalaryInfo(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
