package com.erp.entity;

import com.erp.entity.enums.WelfareType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 복리후생 엔티티
 * 직원별 복리후생 내역을 관리 (교육지원금 등)
 */
@Entity
@Table(name = "welfare")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Welfare extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "welfareId")
    private Long id;
    
    // 직원 (복리후생을 받는 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;
    
    // 복리후생 유형
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WelfareType welfareType;
    
    // 지급 월 (예: 2025-11)
    @Column(nullable = false)
    private YearMonth paymentMonth;
    
    // 지급 금액
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    // 지급일
    private LocalDate paymentDate;
    
    // 비고 (선택사항)
    @Column(length = 500)
    private String note;
    
    // 승인자 (복리후생을 승인한 관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedBy")
    private Employee approver;
    
    // 승인 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean isApproved = false;
}
