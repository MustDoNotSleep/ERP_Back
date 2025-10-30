package com.erp.dto;

import com.erp.entity.Department;
import lombok.Builder;
import lombok.Getter;

public class DepartmentDto {
    
    @Getter
    @Builder
    public static class Request {
        private String departmentName;
        private String teamName;
        private boolean isManagement;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String departmentName;
        private String teamName;
        private boolean isManagement;
        private int employeeCount;
        
        public static Response from(Department department) {
            return Response.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .teamName(department.getTeamName())
                .isManagement(department.isManagement())
                .employeeCount(department.getEmployees().size())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String departmentName;
        private String teamName;
        private boolean isManagement;
    }
}