package com.erp.dto;

import com.erp.entity.WorkEvaluation;
import lombok.*;

public class WorkEvaluationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long employeeId;
        private String name;
        private Integer year;
        private String quarter;
        
        private String teamName;     // 부서명
        
        // [수정 완료] 화면 표시용이므로 '직급명'이 들어가는 게 맞습니다.
        private String positionName; 
        
        private Integer workAttitude;
        private Integer goalAchievement;
        private Integer collaboration;
        private String contribution;
        private String comment;

        public static Response from(WorkEvaluation evaluation) {
            var emp = evaluation.getEmployee();
            var dept = (emp != null) ? emp.getDepartment() : null;
            var pos = (emp != null) ? emp.getPosition() : null; // 직급 정보
            var evaluator = evaluation.getEvaluator();

            String evaluatorInfo = (evaluator != null) 
                ? evaluator.getId() + " - " + evaluator.getName() 
                : "-";

            return Response.builder()
                .id(evaluation.getEvaluationId())
                .employeeId(emp != null ? emp.getId() : null)
                .name(emp != null ? emp.getName() : null)
                .year(evaluation.getEvaluationYear())
                .quarter(evaluation.getEvaluationQuarter() + "분기")
                .teamName(dept != null ? dept.getTeamName() : null)
                
                // [매핑] Position 엔티티의 getPositionName() (예: "부장", "대리") 사용
                .positionName(pos != null ? pos.getPositionName() : null) 
                
                .workAttitude(evaluation.getAttitudeScore())
                .goalAchievement(evaluation.getAchievementScore())
                .collaboration(evaluation.getCollaborationScore())
                .contribution(evaluation.getContributionGrade())
                .comment(evaluatorInfo) 
                .build();
        }
    }

    // Request DTO는 그대로 유지 (프론트 필드명 기준)
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private Integer year;
        private String quarter;
        private Integer workAttitude;
        private Integer goalAchievement;
        private Integer collaboration;
        private String contribution;
        private String comment;
        private String status;
        private Long evaluatorId;
    }
}