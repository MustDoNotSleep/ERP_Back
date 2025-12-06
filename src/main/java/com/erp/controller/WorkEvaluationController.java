package com.erp.controller;

import com.erp.dto.WorkEvaluationDto;
import com.erp.service.WorkEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hr/evaluations")
@RequiredArgsConstructor
public class WorkEvaluationController {

    private final WorkEvaluationService workEvaluationService;

    /**
     * 1. 평가 목록 조회 (프론트엔드 검색 필터 연동)
     * - 인사팀(HR_MANAGER) 또는 관리자(ADMIN)만 전체 조회 가능
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<WorkEvaluationDto.Response>> getEvaluations(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String quarter,
            @RequestParam(required = false) String departmentName
    ) {
        List<WorkEvaluationDto.Response> result = workEvaluationService.getEvaluations(year, quarter, departmentName);
        return ResponseEntity.ok(result);
    }

    /**
     * 2. 부서별 평가 내역 조회
     * - 인사팀, 관리자, 또는 해당 부서의 관리자만 접근 가능
     * (Service 내부 로직과 별개로, 컨트롤러 진입 단계에서 1차 방어)
     */
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or hasRole('DEPT_MANAGER')") 
    public ResponseEntity<List<WorkEvaluationDto.Response>> getEvaluationsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam Integer year,
            @RequestParam Integer quarter,
            @RequestParam Long employeeId
    ) {
        List<WorkEvaluationDto.Response> result = workEvaluationService.getEvaluationsByDepartment(departmentId, year, quarter, employeeId);
        return ResponseEntity.ok(result);
    }

    /**
     * 3. 직원: 본인 평가 조회
     * - 본인(#employeeId == authentication.principal.id) 또는 인사팀만 조회 가능
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<WorkEvaluationDto.Response> getMyEvaluation(
            @RequestParam Long employeeId,
            @RequestParam Integer year,
            @RequestParam Integer quarter
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.getMyEvaluation(employeeId, year, quarter);
        return ResponseEntity.ok(result);
    }

    /**
     * 4. 평가 저장/제출 (신규 등록)
     * - 인사팀 또는 관리자 권한 필요 (상황에 따라 평가자 권한 추가 가능)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'DEPT_MANAGER')")
    public ResponseEntity<WorkEvaluationDto.Response> createEvaluation(
            @RequestParam Long employeeId,
            @RequestBody WorkEvaluationDto.UpdateRequest request
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.saveOrSubmitEvaluation(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 5. 평가 수정 (임시저장 -> 수정 or 제출)
     * - 평가를 작성한 사람(평가자) 또는 인사팀
     */
    @PutMapping("/{evaluationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'DEPT_MANAGER')")
    public ResponseEntity<WorkEvaluationDto.Response> updateEvaluation(
            @PathVariable Long evaluationId,
            @RequestBody WorkEvaluationDto.UpdateRequest request
    ) {
        WorkEvaluationDto.Response result = workEvaluationService.updateEvaluation(evaluationId, request);
        return ResponseEntity.ok(result);
    }
}