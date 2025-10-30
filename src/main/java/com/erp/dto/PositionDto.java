package com.erp.dto;

import com.erp.entity.Position;
import lombok.Builder;
import lombok.Getter;

public class PositionDto {
    
    @Getter
    @Builder
    public static class Request {
        private String positionName;
        private Integer positionLevel;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String positionName;
        private Integer positionLevel;
        private int employeeCount;
        
        public static Response from(Position position) {
            return Response.builder()
                .id(position.getId())
                .positionName(position.getPositionName())
                .positionLevel(position.getPositionLevel())
                .employeeCount(position.getEmployees().size())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String positionName;
        private Integer positionLevel;
    }
}
