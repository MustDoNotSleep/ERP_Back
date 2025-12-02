package com.erp.entity;

import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus;
import com.erp.entity.enums.RewardType;
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
public class Rewards extends BaseEntity { // BaseEntity에도 createdAt 매핑 확인 필요!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rewardId") // DB 컬럼명 명시
    private Long rewardId;

    // --- 1. 대상자 정보 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false) // FK 컬럼명 명시
    private Employee employee;

    // --- 2. 포상 상세 정보 (여기가 핵심!) ---
    
    @Column(name = "rewardDate", nullable = false) // ⭐ DB의 'rewardDate'와 매핑
    private LocalDate rewardDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewardType", nullable = false) // ⭐ DB의 'rewardType'와 매핑
    private RewardType rewardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewardItem", nullable = false) // ⭐ DB의 'rewardItem'와 매핑
    private RewardItem rewardItem;

    @Column(name = "rewardValue") // ⭐ DB의 'rewardValue'와 매핑
    private String rewardValue;

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
    @JoinColumn(name = "approverId") // FK 컬럼명 명시
    private Employee approver;

    @Column(name = "approvedAt") // ⭐ DB의 'approvedAt' (카멜케이스) 매핑
    private LocalDateTime approvedAt;

    // (비즈니스 로직 메서드는 그대로 두시면 됩니다)
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