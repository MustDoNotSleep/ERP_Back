package com.erp.dto;

import com.erp.entity.Course;
import com.erp.entity.enums.CourseType;
import com.erp.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CourseDto {
    
    @Getter
    @Builder
    public static class Request {
        private String courseName;
        private String completionCriteria;
        private Integer capacity;
        private CourseType courseType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String objective;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String courseName;
        private String completionCriteria;
        private Integer capacity;
        private CourseType courseType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String objective;
        private String creatorName;
        private RequestStatus status;
        private String approverName;
        private LocalDateTime processedDate;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(Course course) {
            return Response.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .completionCriteria(course.getCompletionCriteria())
                .capacity(course.getCapacity())
                .courseType(course.getCourseType())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .objective(course.getObjective())
                .creatorName(course.getCreator() != null ? 
                    course.getCreator().getName() : null)
                .status(course.getStatus())
                .approverName(course.getApprover() != null ?
                    course.getApprover().getName() : null)
                .processedDate(course.getProcessedDate())
                .comment(course.getComment())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String courseName;
        private String completionCriteria;
        private Integer capacity;
        private LocalDate startDate;
        private LocalDate endDate;
        private String objective;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        private boolean approved;
        private String comment;
    }
}