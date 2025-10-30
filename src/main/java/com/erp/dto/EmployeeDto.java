package com.erp.dto;

import com.erp.entity.Employee;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDto {
    
    @Getter
    @Builder
    public static class Request {
        private String name;
        private String nameEng;
        private String email;
        private String password;
        private String rrn;
        private String phone;
        private String address;
        private String addressDetail;
        private LocalDate birthDate;
        private LocalDate hireDate;
        private LocalDate quitDate;
        private String internalNumber;
        private String departmentId;
        private String positionId;
        private String familyCertificate;
        private List<String> employeeType;
        private List<String> nationality;
    }
    
    @Getter
    @Builder
    public static class Response {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private LocalDate birthDate;
        private LocalDate hireDate;
        private String departmentName;
        private String positionName;
        private List<String> roles;
        
        public static Response from(Employee employee) {
            return Response.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .birthDate(employee.getBirthDate())
                .hireDate(employee.getHireDate())
                .departmentName(employee.getDepartment() != null ? 
                    employee.getDepartment().getName() : null)
                .positionName(employee.getPosition() != null ? 
                    employee.getPosition().getName() : null)
                .roles(employee.getRoles())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String phone;
        private String address;
        private String departmentId;
        private String positionId;
    }
}