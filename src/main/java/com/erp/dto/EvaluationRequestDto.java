package com.erp.dto;

import com.erp.entity.enums.EvaluationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor // ⭐ 기본 생성자 추가
@AllArgsConstructor // ⭐ 전체 생성자 추가
public class EvaluationRequestDto {

    // --- [기존: 입력(Request)용 필드] ---
    private String seasonName;     // 예: 2025년 1분기

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private EvaluationType evaluationType; // KPI, LEADERSHIP

    private Long targetDepartmentId;   // departments.departmentId
    private Long targetPositionId;     // positions.positionId

    private Long createdById;          // 정책 생성자(관리자 사번)
    
    // --- [추가: 가중치 (입력용)] ---
    private Integer kpiWeight;
    private Integer leadershipWeight;

    private Long policyId;

    // 진행 현황 조회 시 필요
    private Long totalCount;
    private Long completedCount;

}