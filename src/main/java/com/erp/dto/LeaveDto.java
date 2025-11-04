package com.erp.dto;

import com.erp.entity.enums.LeaveDuration;
import com.erp.entity.enums.LeaveStatus;
import com.erp.entity.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class LeaveDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private LeaveType type;
        private LeaveDuration duration;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long employeeId;
        private String employeeName;
        private String departmentName;
        private LeaveType type;
        private LeaveDuration duration;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private LeaveStatus status;
        private Long approvedById;
        private String approvedByName;
        private LocalDate approvedAt;
        private LocalDate createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        private Boolean approved; // true: 승인, false: 반려
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private Long employeeId;
        private String employeeName;
        private Integer year;
        private Double totalAnnualLeave;       // 연차 총 일수
        private Double usedAnnualLeave;        // 사용한 연차
        private Double remainingAnnualLeave;   // 남은 연차
        private Integer totalSickLeave;        // 병가 일수
        private Integer totalMaternityLeave;   // 출산휴가 일수
        private Integer totalBereavementLeave; // 조의 일수
    }
}
