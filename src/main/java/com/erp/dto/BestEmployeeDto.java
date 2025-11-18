package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestEmployeeDto {
    private String name;    // 추천된 직원 이름
    private String reason;  // 추천 사유 (LLM 결과)
    private Integer rank;   // 추천 순위 (1,2,3...)
}
