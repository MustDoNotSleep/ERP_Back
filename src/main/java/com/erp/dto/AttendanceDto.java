package com.erp.dto;

import com.erp.entity.enums.AttendanceType;
import com.erp.entity.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AttendanceDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long employeeId;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private AttendanceType attendanceType;
        private String note;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long employeeId;
        private String employeeName;
        private String departmentName;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private AttendanceType attendanceType;
        private String note;
        private Double workHours;
        private Double overtimeHours;
        private LocalDateTime createdAt;
        
        // 휴가 정보
        private Boolean isOnLeave;
        private Long leaveId;
        private LeaveType leaveType;
        private String leaveReason;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckInRequest {
        private AttendanceType attendanceType;
        private String note;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckOutRequest {
        private String note;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private Long employeeId;
        private String employeeName;
        private Integer totalDays;
        private Integer normalDays;
        private Integer lateDays;
        private Integer earlyLeaveDays;
        private Integer absentDays;
        private Integer remoteDays;
        private Integer overtimeDays;
        private Integer weekendWorkDays;
        private Integer holidayWorkDays;
        private Double totalWorkHours;
        private Double totalOvertimeHours;
    }
}
