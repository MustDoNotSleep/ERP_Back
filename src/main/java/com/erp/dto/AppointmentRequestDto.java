package com.erp.dto;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.entity.enums.Appointment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AppointmentRequestDto {
    
    @Getter
    @Builder
    public static class Request {
        private String employeeId;
        private Appointment.Type appointmentType;
        private String reason;
        private String details;
    }
    
    @Getter
    @Builder
    public static class Response {
        private String id;
        private String employeeName;
        private String departmentName;
        private Appointment.Type appointmentType;
        private String reason;
        private String details;
        private ApplicationStatus status;
        private String approverName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(AppointmentRequest request) {
            return Response.builder()
                .id(request.getId())
                .employeeName(request.getEmployee().getName())
                .departmentName(request.getEmployee().getDepartment().getName())
                .appointmentType(request.getAppointmentType())
                .reason(request.getReason())
                .details(request.getDetails())
                .status(request.getStatus())
                .approverName(request.getApprover() != null ? 
                    request.getApprover().getName() : null)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class ApprovalRequest {
        private String approverId;
        private boolean approved;
        private String comment;
    }
}