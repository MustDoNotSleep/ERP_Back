package com.erp.controller;

import com.erp.dto.PageResponse;
import com.erp.dto.PositionDto;
import com.erp.service.PositionService;
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
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<PositionDto.Response>>> getAllPositions(Pageable pageable) {
        Page<PositionDto.Response> positions = positionService.getAllPositions(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(positions)));
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PositionDto.Response>>> getAllPositionsList() {
        List<PositionDto.Response> positions = positionService.getAllPositionsList();
        return ResponseEntity.ok(ApiResponse.success(positions));
    }
    
    /**
     * 중복 제거된 직급명 목록 (드롭다운용)
     * GET /api/positions/unique-names
     */
    @GetMapping("/unique-names")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getUniquePositionNames() {
        List<String> uniqueNames = positionService.getUniquePositionNames();
        return ResponseEntity.ok(ApiResponse.success(uniqueNames));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PositionDto.Response>> getPositionById(@PathVariable Long id) {
        PositionDto.Response position = positionService.getPositionById(id);
        return ResponseEntity.ok(ApiResponse.success(position));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<PositionDto.Response>> createPosition(
            @Valid @RequestBody PositionDto.Request request) {
        PositionDto.Response created = positionService.createPosition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<ApiResponse<PositionDto.Response>> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody PositionDto.UpdateRequest request) {
        PositionDto.Response updated = positionService.updatePosition(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
