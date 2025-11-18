package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {
    private Integer evaluationYear;    // 평가 연도
    private Integer evaluationQuarter; // 평가 분기
    private Long departmentId;         // 부서 ID (선택)
    private Integer topN;              // 추천 인원 수 (예: 3)
}
