package com.erp.dto;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.enums.AppointmentType;
import com.erp.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentRequestDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long targetEmployeeId;
        private AppointmentType appointmentType;
        private LocalDate effectiveDate;
        private Long newDepartmentId;
        private Long newPositionId;
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
        private String newPositionName;
        private LocalDate effectiveDate;
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
                .newPositionName(request.getNewPosition() != null ?
                    request.getNewPosition().getPositionName() : null)
                .effectiveDate(request.getEffectiveDate())
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        private boolean approved;
        private String comment;
    }
}