package com.erp.entity;

import com.erp.entity.enums.SalaryStatus;
import com.erp.util.TaxCalculator;
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
    private BigDecimal bonus; // 보너스
    
    @Column(length = 500)
    private String bonusReason; // 보너스 지급 사유
    
    @Column(length = 1000)
    private String bonusAttachment; // 보너스 관련 첨부파일 경로/URL

    // 세금 및 공제
    private BigDecimal incomeTax; // 소득세
    private BigDecimal localIncomeTax; // 지방소득세 (소득세의 10%)
    private BigDecimal nationalPension; // 국민연금
    private BigDecimal healthInsurance; // 건강보험
    private BigDecimal employmentInsurance; // 고용보험
    private BigDecimal otherDeductions; // 기타 공제
    
    private BigDecimal totalSalary; // 총 급여
    private BigDecimal netSalary; // 실수령액
    
    @Column(nullable = false)
    private SalaryStatus salaryStatus; // 급여 상태
    
    // Business methods
        // Setter methods for SalaryService updateSalary
        public void setPaymentDate(YearMonth paymentDate) { this.paymentDate = paymentDate; }
        public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
        public void setOvertimeAllowance(BigDecimal overtimeAllowance) { this.overtimeAllowance = overtimeAllowance; }
        public void setNightAllowance(BigDecimal nightAllowance) { this.nightAllowance = nightAllowance; }
        public void setBonus(BigDecimal bonus) { this.bonus = bonus; }
        public void setBonusReason(String bonusReason) { this.bonusReason = bonusReason; }
        public void setBonusAttachment(String bonusAttachment) { this.bonusAttachment = bonusAttachment; }
        public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }
        public void setLocalIncomeTax(BigDecimal localIncomeTax) { this.localIncomeTax = localIncomeTax; }
        public void setNationalPension(BigDecimal nationalPension) { this.nationalPension = nationalPension; }
        public void setHealthInsurance(BigDecimal healthInsurance) { this.healthInsurance = healthInsurance; }
        public void setEmploymentInsurance(BigDecimal employmentInsurance) { this.employmentInsurance = employmentInsurance; }
        public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }
        public void setSalaryStatus(SalaryStatus salaryStatus) { this.salaryStatus = salaryStatus; }

    public void calculateTotal() {
        this.totalSalary = baseSalary
            .add(overtimeAllowance != null ? overtimeAllowance : BigDecimal.ZERO)
            .add(nightAllowance != null ? nightAllowance : BigDecimal.ZERO)
            .add(bonus != null ? bonus : BigDecimal.ZERO);
    }
    
    /**
     * 세금 및 4대보험 자동 계산 (간이세액표 기반)
     * Request로 전달된 값이 있으면 그 값을 우선 사용하고,
     * 없으면 자동 계산된 값을 사용
     */
    public void calculateTaxAndInsurance() {
        // 총 급여 먼저 계산
        if (this.totalSalary == null) {
            calculateTotal();
        }
        
        // 소득세 (수동 입력값이 없으면 자동 계산)
        if (this.incomeTax == null) {
            this.incomeTax = TaxCalculator.calculateIncomeTax(this.totalSalary);
        }
        
        // 지방소득세 (항상 소득세의 10%로 자동 계산)
        this.localIncomeTax = TaxCalculator.calculateLocalIncomeTax(this.incomeTax);
        
        // 국민연금 (수동 입력값이 없으면 자동 계산)
        if (this.nationalPension == null) {
            this.nationalPension = TaxCalculator.calculateNationalPension(this.totalSalary);
        }
        
        // 건강보험 (수동 입력값이 없으면 자동 계산)
        if (this.healthInsurance == null) {
            this.healthInsurance = TaxCalculator.calculateHealthInsurance(this.totalSalary);
        }
        
        // 고용보험 (수동 입력값이 없으면 자동 계산)
        if (this.employmentInsurance == null) {
            this.employmentInsurance = TaxCalculator.calculateEmploymentInsurance(this.totalSalary);
        }
    }
    
    /**
     * 지방소득세 자동 계산 (소득세의 10%)
     * @deprecated calculateTaxAndInsurance() 사용 권장
     */
    @Deprecated
    public void calculateLocalIncomeTax() {
        if (incomeTax != null) {
            this.localIncomeTax = TaxCalculator.calculateLocalIncomeTax(this.incomeTax);
        } else {
            this.localIncomeTax = BigDecimal.ZERO;
        }
    }

    public void calculateNetSalary() {
        // 세금 및 4대보험 자동 계산
        calculateTaxAndInsurance();
        
        BigDecimal deductions = (incomeTax != null ? incomeTax : BigDecimal.ZERO)
            .add(localIncomeTax != null ? localIncomeTax : BigDecimal.ZERO)
            .add(nationalPension != null ? nationalPension : BigDecimal.ZERO)
            .add(healthInsurance != null ? healthInsurance : BigDecimal.ZERO)
            .add(employmentInsurance != null ? employmentInsurance : BigDecimal.ZERO)
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