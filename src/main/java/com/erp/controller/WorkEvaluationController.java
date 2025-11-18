package com.erp.controller;

import com.erp.dto.WorkEvaluationDto;
import com.erp.service.WorkEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/work-evaluations")
@RequiredArgsConstructor
public class WorkEvaluationController {
    private final WorkEvaluationService workEvaluationService;

    // 1. 관리자: 부서별 평가 내역 조회
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_HR')")
    public ResponseEntity<ApiResponse<List<WorkEvaluationDto.Response>>> getEvaluationsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam("evaluationYear") Integer evaluationYear,
            @RequestParam("evaluationQuarter") Integer evaluationQuarter,
            @RequestParam Long employeeId // 관리자 본인 ID
    ) {
        List<WorkEvaluationDto.Response> result = workEvaluationService.getEvaluationsByDepartment(
                departmentId, evaluationYear, evaluationQuarter, employeeId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 2. 직원/관리자: 특정 직원 평가 조회 (분기별 1회)
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WorkEvaluationDto.Response>> getEvaluationByEmployee(
            @PathVariable Long employeeId,
            @RequestParam("evaluationYear") Integer evaluationYear,
            @RequestParam("evaluationQuarter") Integer evaluationQuarter
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.getMyEvaluation(
                employeeId, evaluationYear, evaluationQuarter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 3. 직원: 평가 저장/제출 (분기별 1회)
    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WorkEvaluationDto.Response>> saveOrSubmitEvaluation(
            @PathVariable Long employeeId,
            @RequestBody WorkEvaluationDto.UpdateRequest request
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.saveOrSubmitEvaluation(employeeId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 4. 평가 수정 (제출 전까지만 가능)
    @PutMapping("/{evaluationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WorkEvaluationDto.Response>> updateEvaluation(
            @PathVariable Long evaluationId,
            @RequestBody WorkEvaluationDto.UpdateRequest request
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.updateEvaluation(evaluationId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}