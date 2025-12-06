package com.erp.entity;

import com.erp.entity.converter.RewardValueConverter;
import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus; // ✅ 임포트 확인!
import com.erp.entity.enums.RewardType;
import com.erp.entity.enums.RewardValue;  // ✅ 임포트 확인!
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Rewards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rewardId")
    private Long rewardId;

    // --- 1. 대상자 정보 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    // --- 2. 포상 상세 정보 ---
    
    @Column(name = "rewardDate", nullable = false)
    private LocalDate rewardDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewardType", nullable = false)
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewardItem", nullable = false)
    private RewardItem rewardItem;

    // ✅ [수정] Enum 타입이므로 @Enumerated(EnumType.STRING) 필수!
    @Convert(converter = RewardValueConverter.class)
    @Column(name = "rewardValue")
    private RewardValue rewardValue;

    @Column(name = "amount")
    private Double amount;

    @Lob
    @Column(name = "reason")
    private String reason;

    // --- 3. 결재 상태 ---
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private RewardStatus status = RewardStatus.PENDING;

    // --- 4. 승인자 정보 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approverId")
    private Employee approver;

    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;

    // --- 비즈니스 로직 ---
    public void approve(Employee approver) {
        this.status = RewardStatus.APPROVED;
        this.approver = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(Employee approver) {
        this.status = RewardStatus.REJECTED;
        this.approver = approver;
        this.approvedAt = LocalDateTime.now();
    }
}