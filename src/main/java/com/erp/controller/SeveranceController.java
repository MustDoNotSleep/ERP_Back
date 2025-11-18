package com.erp.controller;

import com.erp.dto.SeveranceDto;
import com.erp.service.SeveranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/severance")
@RequiredArgsConstructor
public class SeveranceController {

    private final SeveranceService severanceService;

    /**
     * 퇴직금 계산
     * 
     * @param request employeeId (필수), severanceDate (선택, null이면 오늘 기준)
     * @return 퇴직금 계산 결과
     * 
     * Example:
     * POST /api/severance/calculate
     * {
     *   "employeeId": 1,
     *   "severanceDate": "2025-12-31"  // 선택사항
     * }
     */
    @PostMapping("/calculate")
    public ResponseEntity<SeveranceDto.CalculationResponse> calculateSeverancePay(
            @RequestBody SeveranceDto.CalculationRequest request) {
        
        SeveranceDto.CalculationResponse response = severanceService.calculateSeverancePay(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 직원의 퇴직금 조회 (간편 버전)
     * 
     * @param employeeId 직원 ID
     * @return 퇴직금 계산 결과 (오늘 날짜 기준)
     * 
     * Example:
     * GET /api/severance/employee/1
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<SeveranceDto.CalculationResponse> getSeveranceByEmployee(
            @PathVariable Long employeeId) {
        
        SeveranceDto.CalculationRequest request = SeveranceDto.CalculationRequest.builder()
                .employeeId(employeeId)
                .build();
        
        SeveranceDto.CalculationResponse response = severanceService.calculateSeverancePay(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 직원의 퇴직금 조회 (기준일 지정)
     * 
     * @param employeeId 직원 ID
     * @param severanceDate 기준일 (YYYY-MM-DD)
     * @return 퇴직금 계산 결과
     * 
     * Example:
     * GET /api/severance/employee/1?severanceDate=2025-12-31
     */
    @GetMapping("/employee/{employeeId}/by-date")
    public ResponseEntity<SeveranceDto.CalculationResponse> getSeveranceByDate(
            @PathVariable Long employeeId,
            @RequestParam String severanceDate) {
        
        SeveranceDto.CalculationRequest request = SeveranceDto.CalculationRequest.builder()
                .employeeId(employeeId)
                .severanceDate(java.time.LocalDate.parse(severanceDate))
                .build();
        
        SeveranceDto.CalculationResponse response = severanceService.calculateSeverancePay(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 직원 예상 퇴직금 조회
     * 
     * @return 전체 직원의 예상 퇴직금 목록 (오늘 기준)
     * 
     * Example:
     * GET /api/severance/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<SeveranceDto.CalculationResponse>> getAllSeverance() {
        List<SeveranceDto.CalculationResponse> responses = severanceService.calculateAllSeverance();
        return ResponseEntity.ok(responses);
    }

    /**
     * 퇴직자 목록 및 퇴직금 조회
     * 
     * @param year 퇴직 연도 (선택, null이면 전체)
     * @return 퇴직자 목록 및 퇴직금
     * 
     * Example:
     * GET /api/severance/retirements
     * GET /api/severance/retirements?year=2025
     */
    @GetMapping("/retirements")
    public ResponseEntity<List<SeveranceDto.CalculationResponse>> getRetirementSeverance(
            @RequestParam(required = false) Integer year) {
        
        List<SeveranceDto.CalculationResponse> responses = severanceService.calculateRetirementSeverance(year);
        return ResponseEntity.ok(responses);
    }
}
