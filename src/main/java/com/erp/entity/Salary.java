// package com.erp.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.math.BigDecimal;
// import java.time.YearMonth;

// @Entity
// @Table(name = "salary")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Builder
// public class Salary extends BaseEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "salaryId")
//     private Long id;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employeeId")
//     private Employee employee;
    
//     @Column(nullable = false)
//     private YearMonth paymentDate; // 급여 지급 날짜
    
//     @Column(nullable = false)
//     private BigDecimal baseSalary; // 기본 급여
    
//     private BigDecimal overtime; // 초과 근무 수당
//     private BigDecimal bonus; // 보너스
//     private BigDecimal mealAllowance; // 식대
//     private BigDecimal transportAllowance; // 교통비
    
//     // 세금 및 공제
//     private BigDecimal incomeTax;// 소득세
//     private BigDecimal nationalPension; // 국민연금
//     private BigDecimal healthInsurance; // 건강보험
//     private BigDecimal employmentInsurance; // 고용보험
    
//     @Column(name = "total_salary")
//     private BigDecimal totalSalary; // 총 급여
    
//     @Column(name = "net_salary")
//     private BigDecimal netSalary; // 실수령액
    
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private SalaryStatus status; // 급여 상태
    
//     public enum SalaryStatus {
//         DRAFT, CONFIRMED, PAID // 초안, 확정, 지급됨
//     }
    
//     // Business methods
//     public void calculateTotal() {
//         this.totalSalary = baseSalary
//             .add(overtime != null ? overtime : BigDecimal.ZERO)
//             .add(bonus != null ? bonus : BigDecimal.ZERO)
//             .add(mealAllowance != null ? mealAllowance : BigDecimal.ZERO)
//             .add(transportAllowance != null ? transportAllowance : BigDecimal.ZERO);
//     }
    
//     public void calculateNetSalary() {
//         BigDecimal deductions = incomeTax
//             .add(nationalPension != null ? nationalPension : BigDecimal.ZERO)
//             .add(healthInsurance != null ? healthInsurance : BigDecimal.ZERO)
//             .add(employmentInsurance != null ? employmentInsurance : BigDecimal.ZERO);
        
//         this.netSalary = totalSalary.subtract(deductions);
//     }
    
//     public void confirm() {
//         calculateTotal();
//         calculateNetSalary();
//         this.status = SalaryStatus.CONFIRMED;
//     }
    
//     public void markAsPaid() {
//         this.status = SalaryStatus.PAID;
//     }
// }