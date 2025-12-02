package com.erp.entity;

import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus;
import com.erp.entity.enums.RewardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewards") // 테이블 이름
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Rewards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId; // DB 컬럼명: rewardId (일치하므로 @Column 생략 가능)

    // --- 1. 대상자 정보 (N:1) ---
    // DB 컬럼명: employeeId
    // 객체로 매핑하되, 연결할 컬럼 이름(name)을 DB와 똑같이 'employeeId'로 지정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    // --- 2. 포상 상세 정보 ---
    // 아래 필드들은 DB 컬럼명과 변수명이 토씨 하나 안 틀리고 같아서 @Column 생략!
    
    @Column(nullable = false)
    private LocalDate rewardDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType; // ENUM (CONTRIBUTION, BEST_EMPLOYEE...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardItem rewardItem; // ENUM (MONEY, VACATION...)

    private String rewardValue;    // 포상 가치 (금액, 일수 등)

    @Lob
    private String reason;         // 포상 사유

    // --- 3. 결재 상태 ---
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RewardStatus status = RewardStatus.PENDING; // 기본값: 대기

    // --- 4. 승인자 정보 (N:1) ---
    // DB 컬럼명: approverId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approverId")
    private Employee approver;

    private LocalDateTime approvedAt;

    // --- 비즈니스 로직 (승인/반려) ---
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