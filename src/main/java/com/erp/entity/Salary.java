package com.erp.entity;

import com.erp.entity.enums.SalaryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "salary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Salary extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salaryId")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    
    @Column(nullable = false)
    private YearMonth paymentDate; // 급여 지급 날짜
    
    @Column(nullable = false)
    private BigDecimal baseSalary; // 기본 급여
    

    private BigDecimal overtimeAllowance; // 야근수당
    private BigDecimal nightAllowance; // 야간수당
    private BigDecimal dutyAllowance; // 당직수당 (nullable)
    private BigDecimal bonus; // 보너스

    // 세금 및 공제
    private BigDecimal incomeTax; // 소득세
    private BigDecimal nationalPension; // 국민연금
    private BigDecimal healthInsurance; // 건강보험
    private BigDecimal employmentInsurance; // 고용보험
    private BigDecimal societyFee; // 상조회비 (nullable)
    private BigDecimal advancePayment; // 가불금 (nullable)
    private BigDecimal otherDeductions; // 기타 공제
    
    @Column(name = "total_salary")
    private BigDecimal totalSalary; // 총 급여
    
    @Column(name = "net_salary")
    private BigDecimal netSalary; // 실수령액
    
    @Column(nullable = false)
    private SalaryStatus salaryStatus; // 급여 상태
    
    // Business methods
        // Setter methods for SalaryService updateSalary
        public void setPaymentDate(YearMonth paymentDate) { this.paymentDate = paymentDate; }
        public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
        public void setOvertimeAllowance(BigDecimal overtimeAllowance) { this.overtimeAllowance = overtimeAllowance; }
        public void setNightAllowance(BigDecimal nightAllowance) { this.nightAllowance = nightAllowance; }
        public void setDutyAllowance(BigDecimal dutyAllowance) { this.dutyAllowance = dutyAllowance; }
        public void setBonus(BigDecimal bonus) { this.bonus = bonus; }
        public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }
        public void setNationalPension(BigDecimal nationalPension) { this.nationalPension = nationalPension; }
        public void setHealthInsurance(BigDecimal healthInsurance) { this.healthInsurance = healthInsurance; }
        public void setEmploymentInsurance(BigDecimal employmentInsurance) { this.employmentInsurance = employmentInsurance; }
        public void setSocietyFee(BigDecimal societyFee) { this.societyFee = societyFee; }
        public void setAdvancePayment(BigDecimal advancePayment) { this.advancePayment = advancePayment; }
        public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }
        public void setSalaryStatus(SalaryStatus salaryStatus) { this.salaryStatus = salaryStatus; }

    public void calculateTotal() {
        this.totalSalary = baseSalary
            .add(overtimeAllowance != null ? overtimeAllowance : BigDecimal.ZERO)
            .add(nightAllowance != null ? nightAllowance : BigDecimal.ZERO)
            .add(dutyAllowance != null ? dutyAllowance : BigDecimal.ZERO)
            .add(bonus != null ? bonus : BigDecimal.ZERO);
    }
    

    public void calculateNetSalary() {
        BigDecimal deductions = (incomeTax != null ? incomeTax : BigDecimal.ZERO)
            .add(nationalPension != null ? nationalPension : BigDecimal.ZERO)
            .add(healthInsurance != null ? healthInsurance : BigDecimal.ZERO)
            .add(employmentInsurance != null ? employmentInsurance : BigDecimal.ZERO)
            .add(societyFee != null ? societyFee : BigDecimal.ZERO)
            .add(advancePayment != null ? advancePayment : BigDecimal.ZERO)
            .add(otherDeductions != null ? otherDeductions : BigDecimal.ZERO);
        this.netSalary = totalSalary.subtract(deductions);
    }
    
    public void confirm() {
        calculateTotal();
        calculateNetSalary();
        this.salaryStatus = SalaryStatus.CONFIRMED;
    }
    
    public void markAsPaid() {
        this.salaryStatus = SalaryStatus.PAID;
    }
}