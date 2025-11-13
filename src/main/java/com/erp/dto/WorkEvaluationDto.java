package com.erp.dto;

import com.erp.entity.WorkEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorkEvaluationDto {

    @Getter
    @Builder
    public static class Response {
        
        private Long evaluationId;
        private Long employeeId;
        private String employeeName;
        private Integer attitudeScore;
        private Integer achievementScore;
        private Integer collaborationScore; // 협업
        private String contributionGrade;   // 기여도 (B)
        private String totalGrade;          // 종합 (T)

        // --- 평가 상태 ---
        private String status;              // "임시저장" 또는 "제출완료" (String)

        public static Response from(WorkEvaluation evaluation) {
            return Response.builder()
                .evaluationId(evaluation.getEvaluationId())
                .employeeId(evaluation.getEmployee().getId()) 
                .employeeName(evaluation.getEmployee().getName())                            
                .attitudeScore(evaluation.getAttitudeScore())
                .achievementScore(evaluation.getAchievementScore())
                .collaborationScore(evaluation.getCollaborationScore())
                .contributionGrade(evaluation.getContributionGrade())
                .totalGrade(evaluation.getTotalGrade())
                .status(evaluation.getStatus())
                .build();
        }
    }

    /**
     * 📨 평가 수정 및 저장/제출 요청 (Client -> Server)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {

        private Integer attitudeScore;
        private Integer achievementScore;
        private Integer collaborationScore;
        private String contributionGrade;
        private String totalGrade;
        

        private String status; 
    }
}