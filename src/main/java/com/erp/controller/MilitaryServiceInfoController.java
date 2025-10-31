package com.erp.controller;

import com.erp.dto.MilitaryServiceInfoDto;
import com.erp.service.MilitaryServiceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/military-service")
@RequiredArgsConstructor
public class MilitaryServiceInfoController {

    private final MilitaryServiceInfoService militaryServiceInfoService;

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<MilitaryServiceInfoDto.Response>> getMilitaryServiceInfoByEmployeeId(
            @PathVariable Long employeeId) {
        Optional<MilitaryServiceInfoDto.Response> militaryServiceInfo = 
                militaryServiceInfoService.getMilitaryServiceInfoByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(militaryServiceInfo.orElse(null)));
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<ApiResponse<MilitaryServiceInfoDto.Response>> createMilitaryServiceInfo(
            @PathVariable Long employeeId,
            @Valid @RequestBody MilitaryServiceInfoDto.Request request) {
        MilitaryServiceInfoDto.Response created = militaryServiceInfoService.createMilitaryServiceInfo(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<MilitaryServiceInfoDto.Response>> updateMilitaryServiceInfo(
            @PathVariable Long id,
            @Valid @RequestBody MilitaryServiceInfoDto.UpdateRequest request) {
        MilitaryServiceInfoDto.Response updated = militaryServiceInfoService.updateMilitaryServiceInfo(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteMilitaryServiceInfo(@PathVariable Long id) {
        militaryServiceInfoService.deleteMilitaryServiceInfo(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
