package com.erp.dto;

import com.erp.entity.CourseApplication;
import com.erp.entity.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CourseApplicationDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long courseId;
        private Long employeeId;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String courseName;
        private String employeeName;
        private String departmentName;
        private LocalDateTime applicationDate;
        private ApplicationStatus status;
        private String processorName;
        private LocalDateTime processedAt;
        private String rejectionReason;
        
        public static Response from(CourseApplication application) {
            return Response.builder()
                .id(application.getId())
                .courseName(application.getCourse().getCourseName())
                .employeeName(application.getEmployee().getName())
                .departmentName(application.getEmployee().getDepartment() != null ?
                    application.getEmployee().getDepartment().getDepartmentName() : null)
                .applicationDate(application.getApplicationDate())
                .status(application.getStatus())
                .processorName(application.getProcessor() != null ? 
                    application.getProcessor().getName() : null)
                .processedAt(application.getProcessedAt())
                .rejectionReason(application.getRejectionReason())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class ApprovalRequest {
        private Long processorId;
        private boolean approved;
        private String rejectionReason;
    }
}
