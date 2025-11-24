package com.erp.dto;

import com.erp.entity.ResignationApplication;
import com.erp.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResignationApplicationDto {
    
    /**
     * 퇴직 신청 생성 요청
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        // employeeId는 선택적 (토큰에서 자동으로 가져옴)
        // 관리자가 대신 신청하는 경우에만 필요
        private Long employeeId;
        
        @NotNull(message = "퇴직 희망일은 필수입니다")
        @Future(message = "퇴직 희망일은 미래 날짜여야 합니다")
        private LocalDate desiredResignationDate;   // 퇴직 희망일
        
        @NotBlank(message = "퇴직 사유는 필수입니다")
        private String reason;                       // 퇴직 사유
        
        private String detailedReason;               // 상세 사유 (선택)
    }
    
    /**
     * 퇴직 신청 승인/반려 요청
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        // processorId는 선택적 (토큰에서 자동으로 가져옴)
        private Long processorId;
        
        @NotNull(message = "승인 여부는 필수입니다")
        private Boolean approved;                    // 승인 여부 (true: 승인, false: 반려)
        
        private String rejectionReason;              // 반려 사유 (반려 시 필수)
        private LocalDate finalResignationDate;      // 최종 퇴사일 (승인 시, null이면 희망일 사용)
    }
    
    /**
     * 퇴직 신청 응답
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private EmployeeDto employee;
        private LocalDate desiredResignationDate;
        private String reason;
        private String detailedReason;
        private ApplicationStatus status;
        private LocalDateTime applicationDate;
        private EmployeeDto processor;
        private LocalDateTime processedAt;
        private String rejectionReason;
        private LocalDate finalResignationDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class EmployeeDto {
            private Long id;
            private String name;
            private String email;
            private String departmentName;
            private String positionName;
            private LocalDate hireDate;
        }
        
        public static Response from(ResignationApplication application) {
            return Response.builder()
                    .id(application.getId())
                    .employee(EmployeeDto.builder()
                            .id(application.getEmployee().getId())
                            .name(application.getEmployee().getName())
                            .email(application.getEmployee().getEmail())
                            .departmentName(application.getEmployee().getDepartment() != null 
                                    ? application.getEmployee().getDepartment().getDepartmentName() 
                                    : null)
                            .positionName(application.getEmployee().getPosition() != null 
                                    ? application.getEmployee().getPosition().getPositionName() 
                                    : null)
                            .hireDate(application.getEmployee().getHireDate())
                            .build())
                    .desiredResignationDate(application.getDesiredResignationDate())
                    .reason(application.getReason())
                    .detailedReason(application.getDetailedReason())
                    .status(application.getStatus())
                    .applicationDate(application.getApplicationDate())
                    .processor(application.getProcessor() != null 
                            ? EmployeeDto.builder()
                                    .id(application.getProcessor().getId())
                                    .name(application.getProcessor().getName())
                                    .email(application.getProcessor().getEmail())
                                    .departmentName(application.getProcessor().getDepartment() != null 
                                            ? application.getProcessor().getDepartment().getDepartmentName() 
                                            : null)
                                    .positionName(application.getProcessor().getPosition() != null 
                                            ? application.getProcessor().getPosition().getPositionName() 
                                            : null)
                                    .hireDate(application.getProcessor().getHireDate())
                                    .build()
                            : null)
                    .processedAt(application.getProcessedAt())
                    .rejectionReason(application.getRejectionReason())
                    .finalResignationDate(application.getFinalResignationDate())
                    .createdAt(application.getCreatedAt())
                    .updatedAt(application.getUpdatedAt())
                    .build();
        }
    }
    
    /**
     * 퇴직 신청 통계
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private Long totalApplications;      // 전체 신청 수
        private Long pendingApplications;    // 대기 중인 신청
        private Long approvedApplications;   // 승인된 신청
        private Long rejectedApplications;   // 반려된 신청
    }
}
