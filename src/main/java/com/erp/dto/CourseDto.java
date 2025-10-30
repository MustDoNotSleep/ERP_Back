package com.erp.dto;

import com.erp.entity.Course;
import com.erp.entity.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class CourseDto {
    
    @Getter
    @Builder
    public static class Request {
        private String title;
        private String description;
        private CourseType type;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructor;
        private String location;
        private Integer capacity;
        private String requirements;
    }
    
    @Getter
    @Builder
    public static class Response {
        private String id;
        private String title;
        private String description;
        private CourseType type;
        private LocalDate startDate;
        private LocalDate endDate;
        private String instructor;
        private String location;
        private Integer capacity;
        private Integer currentParticipants;
        private String requirements;
        private LocalDateTime createdAt;
        
        public static Response from(Course course) {
            return Response.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .type(course.getType())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .instructor(course.getInstructor())
                .location(course.getLocation())
                .capacity(course.getCapacity())
                .currentParticipants(course.getApplications().size())
                .requirements(course.getRequirements())
                .createdAt(course.getCreatedAt())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String title;
        private String description;
        private String instructor;
        private String location;
        private Integer capacity;
        private String requirements;
    }
}