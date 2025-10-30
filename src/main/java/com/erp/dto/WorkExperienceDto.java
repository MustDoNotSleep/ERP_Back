package com.erp.dto;

import com.erp.entity.WorkExperience;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class WorkExperienceDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private String companyName;
        private String jobTitle;
        private String finalPosition;
        private int finalSalary;
        private LocalDate startDate;
        private LocalDate endDate;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private String companyName;
        private String jobTitle;
        private String finalPosition;
        private int finalSalary;
        private LocalDate startDate;
        private LocalDate endDate;
        
        public static Response from(WorkExperience experience) {
            return Response.builder()
                .id(experience.getId())
                .employeeName(experience.getEmployee().getName())
                .companyName(experience.getCompanyName())
                .jobTitle(experience.getJobTitle())
                .finalPosition(experience.getFinalPosition())
                .finalSalary(experience.getFinalSalary())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String companyName;
        private String jobTitle;
        private String finalPosition;
        private int finalSalary;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
