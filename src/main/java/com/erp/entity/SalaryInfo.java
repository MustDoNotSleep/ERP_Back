package com.erp.entity;

import com.erp.entity.enums.BankName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SalaryInfo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SalaryInfo {
    
    // 1. salaryInfoId (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salaryInfoId")
    private Long id;
    
    // 2. employeeId (외래 키: 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee; 

    // 3. bankName (Enum 적용: 은행 이름)
    @Enumerated(EnumType.STRING)
    @Column(name = "bankName")
    private BankName bankName;

    // 4. accountNumber (계좌 번호)
    @Column(length = 50)
    private String accountNumber;

    // 5. 월 기본급
    @Column(name = "monthlyBaseSalary", nullable = false)
    private java.math.BigDecimal monthlyBaseSalary;

    /*
     * 비즈니스 로직 추가 영역 (예: 급여 정보 업데이트 메서드)
     */
}