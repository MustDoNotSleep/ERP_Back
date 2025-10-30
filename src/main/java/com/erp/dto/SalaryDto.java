// package com.erp.dto;

// import com.erp.entity.Salary;
// import lombok.Builder;
// import lombok.Getter;

// import java.math.BigDecimal;
// import java.time.YearMonth;

// public class SalaryDto {
    
//     @Getter
//     @Builder
//     public static class Request {
//         private String employeeId;
//         private YearMonth paymentDate;
//         private BigDecimal baseSalary;
//         private BigDecimal overtime;
//         private BigDecimal bonus;
//         private BigDecimal mealAllowance;
//         private BigDecimal transportAllowance;
//     }
    
//     @Getter
//     @Builder
//     public static class Response {
//         private String id;
//         private String employeeName;
//         private String departmentName;
//         private String positionName;
//         private YearMonth paymentDate;
//         private BigDecimal baseSalary;
//         private BigDecimal overtime;
//         private BigDecimal bonus;
//         private BigDecimal mealAllowance;
//         private BigDecimal transportAllowance;
//         private BigDecimal incomeTax;
//         private BigDecimal nationalPension;
//         private BigDecimal healthInsurance;
//         private BigDecimal employmentInsurance;
//         private BigDecimal totalSalary;
//         private BigDecimal netSalary;
//         private Salary.SalaryStatus status;
        
//         public static Response from(Salary salary) {
//             return Response.builder()
//                 .id(salary.getId())
//                 .employeeName(salary.getEmployee().getName())
//                 .departmentName(salary.getEmployee().getDepartment().getName())
//                 .positionName(salary.getEmployee().getPosition().getName())
//                 .paymentDate(salary.getPaymentDate())
//                 .baseSalary(salary.getBaseSalary())
//                 .overtime(salary.getOvertime())
//                 .bonus(salary.getBonus())
//                 .mealAllowance(salary.getMealAllowance())
//                 .transportAllowance(salary.getTransportAllowance())
//                 .incomeTax(salary.getIncomeTax())
//                 .nationalPension(salary.getNationalPension())
//                 .healthInsurance(salary.getHealthInsurance())
//                 .employmentInsurance(salary.getEmploymentInsurance())
//                 .totalSalary(salary.getTotalSalary())
//                 .netSalary(salary.getNetSalary())
//                 .status(salary.getStatus())
//                 .build();
//         }
//     }
    
//     @Getter
//     @Builder
//     public static class SummaryResponse {
//         private YearMonth paymentDate;
//         private String departmentName;
//         private int employeeCount;
//         private BigDecimal totalAmount;
//         private BigDecimal averageAmount;
//     }
// }