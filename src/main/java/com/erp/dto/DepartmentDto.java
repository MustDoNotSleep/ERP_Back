package com.erp.dto;

import com.erp.entity.Department;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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
        private String id;
        private String departmentName;
        private String teamName;
        private boolean isManagement;
        
        public static Response from(Department department) {
            return Response.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .parentDepartmentName(department.getParentDepartment() != null ? 
                    department.getParentDepartment().getName() : null)
                .childDepartments(department.getChildDepartments().stream()
                    .map(ChildDepartment::from)
                    .collect(Collectors.toList()))
                .employeeCount(department.getEmployees().size())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String departmentName;
        private String teamName;
    }
}