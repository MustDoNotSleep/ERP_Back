package com.erp.controller;

import com.erp.dto.CertificateDto;
import com.erp.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<CertificateDto.Response>>> getCertificatesByEmployeeId(
            @PathVariable Long employeeId) {
        List<CertificateDto.Response> certificates = certificateService.getCertificatesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(certificates));
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<CertificateDto.Response>> createCertificate(
            @PathVariable Long employeeId,
            @Valid @RequestBody CertificateDto.Request request) {
        CertificateDto.Response created = certificateService.createCertificate(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<CertificateDto.Response>> updateCertificate(
            @PathVariable Long id,
            @Valid @RequestBody CertificateDto.UpdateRequest request) {
        CertificateDto.Response updated = certificateService.updateCertificate(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
