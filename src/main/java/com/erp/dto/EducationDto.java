package com.erp.dto;

import com.erp.entity.Education;
import com.erp.entity.enums.Degree;
import com.erp.entity.enums.GraduationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class EducationDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private String schoolName;
        private String major;
        private Degree degree;
        private GraduationStatus graduationStatus;
        private LocalDate admissionDate;
        private LocalDate graduationDate;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private String schoolName;
        private String major;
        private Degree degree;
        private GraduationStatus graduationStatus;
        private LocalDate admissionDate;
        private LocalDate graduationDate;
        
        public static Response from(Education education) {
            return Response.builder()
                .id(education.getId())
                .employeeName(education.getEmployee().getName())
                .schoolName(education.getSchoolName())
                .major(education.getMajor())
                .degree(education.getDegree())
                .graduationStatus(education.getGraduationStatus())
                .admissionDate(education.getAdmissionDate())
                .graduationDate(education.getGraduationDate())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String schoolName;
        private String major;
        private Degree degree;
        private GraduationStatus graduationStatus;
        private LocalDate admissionDate;
        private LocalDate graduationDate;
    }
}
