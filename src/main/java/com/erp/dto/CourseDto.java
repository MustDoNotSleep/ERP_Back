package com.erp.dto;

import com.erp.entity.Course;
import com.erp.entity.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

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
        private Long creatorId;
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
}