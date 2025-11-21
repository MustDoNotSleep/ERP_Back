package com.erp.controller;

import com.erp.dto.AiDto;
import com.erp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hr/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 우수 사원 AI 추천 API
     * GET /hr/ai/recommend?year=2025&quarter=1
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<AiDto.Recommendation>> getRecommendations(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter
    ) {
        // 파라미터 없으면 현재 날짜 기준 자동 설정
        if (year == null) year = LocalDate.now().getYear();
        if (quarter == null) quarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;

        List<AiDto.Recommendation> result = aiService.getAiRecommendations(year, quarter);
        return ResponseEntity.ok(result);
    }
}