package com.erp.controller;

import com.erp.dto.EvaluationRequestDto; // ⭐ 하나로 통일된 DTO 사용
import com.erp.service.EvaluationManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/hr/policy") // 기존 경로 유지
@RequiredArgsConstructor
public class EvaluationManageController {

    private final EvaluationManageService evaluationManageService;

    // ==================================================================
    // 🔍 [GET] 조회 API
    // ==================================================================

    /**
     * 1. 평가 시즌 목록 조회
     * - 반환 타입 변경: List<EvaluationRequestDto>
     */
    @GetMapping("/seasons")
    public ResponseEntity<List<EvaluationRequestDto>> getEvaluationSeasons() {
        return ResponseEntity.ok(evaluationManageService.findAllPolicies());
    }
    

    /**
     * 2. 평가 진행 현황 조회
     * - 반환 타입 변경: EvaluationRequestDto (total, completed 포함)
     */
    @GetMapping("/progress")
    public ResponseEntity<EvaluationRequestDto> getEvaluationProgress(
            @RequestParam(required = false) String seasonName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId
    ) {
        // Service에서 계산된 DTO를 반환
        return ResponseEntity.ok(evaluationManageService.getEvaluationProgress(
                seasonName, departmentId, positionId));
    }

    // ==================================================================
    // 💾 [POST] 저장 API
    // ==================================================================

    /**
     * 3. 평가 정책 생성 + 엑셀 업로드
     */
    @PostMapping(value = "/setup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setupEvaluation(
            @RequestPart("data") EvaluationRequestDto dto,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            evaluationManageService.createEvaluationPolicy(dto, file);
            return ResponseEntity.ok().body("평가 정책과 평가 결과 저장이 완료되었습니다.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생: " + e.getMessage());
        }
    }
}