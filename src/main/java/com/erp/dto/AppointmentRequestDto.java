package com.erp.dto;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.enums.AppointmentType;
import com.erp.entity.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentRequestDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long targetEmployeeId;
        private Long requestingEmployeeId;
        private AppointmentType appointmentType;
        private Long newDepartmentId;
        private LocalDate effectiveStartDate;
        private LocalDate effectiveEndDate;
        private String reason;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String targetEmployeeName;
        private String requestingEmployeeName;
        private AppointmentType appointmentType;
        private String newDepartmentName;
        private LocalDate effectiveStartDate;
        private LocalDate effectiveEndDate;
        private String reason;
        private RequestStatus status;
        private String approverName;
        private LocalDateTime requestDate;
        private LocalDateTime processedDate;
        
        public static Response from(AppointmentRequest request) {
            return Response.builder()
                .id(request.getId())
                .targetEmployeeName(request.getTargetEmployee().getName())
                .requestingEmployeeName(request.getRequestingEmployee().getName())
                .appointmentType(request.getAppointmentType())
                .newDepartmentName(request.getNewDepartment() != null ? 
                    request.getNewDepartment().getDepartmentName() : null)
                .effectiveStartDate(request.getEffectiveStartDate())
                .effectiveEndDate(request.getEffectiveEndDate())
                .reason(request.getReason())
                .status(request.getStatus())
                .approverName(request.getApprover() != null ? 
                    request.getApprover().getName() : null)
                .requestDate(request.getRequestDate())
                .processedDate(request.getProcessedDate())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class ApprovalRequest {
        private Long approverId;
        private boolean approved;
        private String comment;
    }
}