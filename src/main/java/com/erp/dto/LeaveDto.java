// package com.erp.dto;

// import com.erp.entity.Leave;
// import lombok.Builder;
// import lombok.Getter;

// import java.time.LocalDate;

// public class LeaveDto {
    
//     @Getter
//     @Builder
//     public static class Request {
//         private String employeeId;
//         private Leave.LeaveType type;
//         private LocalDate startDate;
//         private LocalDate endDate;
//         private String reason;
//     }
    
//     @Getter
//     @Builder
//     public static class Response {
//         private String id;
//         private String employeeName;
//         private String departmentName;
//         private Leave.LeaveType type;
//         private LocalDate startDate;
//         private LocalDate endDate;
//         private String reason;
//         private Leave.LeaveStatus status;
//         private String approvedBy;
//         private LocalDate approvedAt;
        
//         public static Response from(Leave leave) {
//             return Response.builder()
//                 .id(leave.getId())
//                 .employeeName(leave.getEmployee().getName())
//                 .departmentName(leave.getEmployee().getDepartment().getName())
//                 .type(leave.getType())
//                 .startDate(leave.getStartDate())
//                 .endDate(leave.getEndDate())
//                 .reason(leave.getReason())
//                 .status(leave.getStatus())
//                 .approvedBy(leave.getApprovedBy() != null ? 
//                     leave.getApprovedBy().getName() : null)
//                 .approvedAt(leave.getApprovedAt())
//                 .build();
//         }
//     }
    
//     @Getter
//     @Builder
//     public static class ApprovalRequest {
//         private String approverId;
//         private boolean approved;
//     }
// }