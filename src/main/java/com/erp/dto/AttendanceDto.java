// package com.erp.dto;

// import com.erp.entity.Attendance;
// import lombok.Builder;
// import lombok.Getter;

// import java.time.LocalDateTime;

// public class AttendanceDto {
    
//     @Getter
//     @Builder
//     public static class CheckInRequest {
//         private String employeeId;
//         private LocalDateTime checkInTime;
//         private String note;
//     }
    
//     @Getter
//     @Builder
//     public static class CheckOutRequest {
//         private String employeeId;
//         private LocalDateTime checkOutTime;
//         private String note;
//     }
    
//     @Getter
//     @Builder
//     public static class Response {
//         private String id;
//         private String employeeName;
//         private String departmentName;
//         private LocalDateTime checkIn;
//         private LocalDateTime checkOut;
//         private Attendance.AttendanceType type;
//         private String note;
//         private Double workHours;
        
//         public static Response from(Attendance attendance) {
//             return Response.builder()
//                 .id(attendance.getId())
//                 .employeeName(attendance.getEmployee().getName())
//                 .departmentName(attendance.getEmployee().getDepartment().getName())
//                 .checkIn(attendance.getCheckIn())
//                 .checkOut(attendance.getCheckOut())
//                 .type(attendance.getType())
//                 .note(attendance.getNote())
//                 .workHours(attendance.getWorkHours())
//                 .build();
//         }
//     }
    
//     @Getter
//     @Builder
//     public static class DailyStatusResponse {
//         private String employeeName;
//         private String departmentName;
//         private Attendance.AttendanceType status;
//         private LocalDateTime lastCheckIn;
//         private Double currentWorkHours;
//     }
// }