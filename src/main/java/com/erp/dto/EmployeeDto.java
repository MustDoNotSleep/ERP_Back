package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.enums.EmploymentType;
import com.erp.entity.enums.Nationality;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class EmployeeDto {
    
    @Getter
    @Builder
    public static class Request {
        private String name;
        
        @JsonProperty("nameeng")  // 프론트에서 "nameeng"으로 보내도 OK
        private String nameEng;
        
        private String email;
        private String password;
        private String rrn;
        private String phone;
        private String address;
        private String addressDetails;
        private LocalDate birthDate;
        private LocalDate hireDate;
        private LocalDate quitDate;
        private String internalNumber;
        private String departmentName;
        private String teamName;
        private String positionName;
        private String familyCertificate;
        private EmploymentType employmentType;
        private Nationality nationality;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String nameEng;
        private String email;
        private String rrn;
        private String phone;
        private String address;
        private String addressDetails;
        private LocalDate birthDate;
        private LocalDate hireDate;
        private LocalDate quitDate;
        private String internalNumber;
        private String familyCertificate;
        private String departmentName;
        private String teamName;  // 추가
        private String positionName;
        private EmploymentType employmentType;
        private Nationality nationality;
        
        public static Response from(Employee employee) {
            return Response.builder()
                .id(employee.getId())
                .name(employee.getName())
                .nameEng(employee.getNameEng())
                .email(employee.getEmail())
                .rrn(employee.getRrn())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .addressDetails(employee.getAddressDetails())
                .birthDate(employee.getBirthDate())
                .hireDate(employee.getHireDate())
                .quitDate(employee.getQuitDate())
                .internalNumber(employee.getInternalNumber())
                .familyCertificate(employee.getFamilyCertificate())
                .departmentName(employee.getDepartment() != null ? 
                    employee.getDepartment().getDepartmentName() : null)
                .teamName(employee.getDepartment() != null ? 
                    employee.getDepartment().getTeamName() : null)  // 추가
                .positionName(employee.getPosition() != null ? 
                    employee.getPosition().getPositionName() : null)
                .employmentType(employee.getEmploymentType())
                .nationality(employee.getNationality())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String phone;
        private String address;
        private String addressDetails;
        private Long departmentId;
        private Long positionId;
    }
}