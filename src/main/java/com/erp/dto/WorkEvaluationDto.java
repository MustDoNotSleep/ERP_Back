package com.erp.dto;

import com.erp.entity.WorkEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class WorkEvaluationDto {

    @Getter
    @Builder
    public static class Response {
        private Long evaluationId;
        private Long employeeId;
        private String employeeName;
        private Integer evaluationYear;
        private Integer evaluationQuarter;
        private Integer attitudeScore;
        private Integer achievementScore;
        private Integer collaborationScore;
        private String contributionGrade;
        private String totalGrade;
        private String status;
        private Long evaluatorId;
        private String evaluatorName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(WorkEvaluation evaluation) {
            return Response.builder()
                .evaluationId(evaluation.getEvaluationId())
                .employeeId(evaluation.getEmployee().getId())
                .employeeName(evaluation.getEmployee().getName())
                .evaluationYear(evaluation.getEvaluationYear())
                .evaluationQuarter(evaluation.getEvaluationQuarter())
                .attitudeScore(evaluation.getAttitudeScore())
                .achievementScore(evaluation.getAchievementScore())
                .collaborationScore(evaluation.getCollaborationScore())
                .contributionGrade(evaluation.getContributionGrade())
                .totalGrade(evaluation.getTotalGrade())
                .status(evaluation.getStatus())
                .evaluatorId(evaluation.getEvaluator() != null ? evaluation.getEvaluator().getId() : null)
                .evaluatorName(evaluation.getEvaluator() != null ? evaluation.getEvaluator().getName() : null)
                .createdAt(evaluation.getCreatedAt())
                .updatedAt(evaluation.getUpdatedAt())
                .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private Integer evaluationYear;
        private Integer evaluationQuarter;
        private Integer attitudeScore;
        private Integer achievementScore;
        private Integer collaborationScore;
        private String contributionGrade;
        private String totalGrade;
        private String status;
        private Long evaluatorId;
    }
}