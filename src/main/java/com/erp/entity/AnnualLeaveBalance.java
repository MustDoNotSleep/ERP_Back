package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 연차 잔여 관리 엔티티
 * - 1년 미만: 월별 1일씩 발생
 * - 1년 이상: 연 15일 (출근율 80% 이상)
 * - 3년 이상: 2년마다 1일씩 추가
 */
@Entity
@Table(name = "annual_leave_balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnnualLeaveBalance extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private Integer year; // 연차 발생 연도
    
    @Column(nullable = false)
    private Double totalDays; // 총 연차 일수
    
    @Column(nullable = false)
    private Double usedDays; // 사용한 연차 일수
    
    @Column(nullable = false)
    private Double remainingDays; // 남은 연차 일수
    
    private LocalDate expiryDate; // 연차 만료일 (2년)
    
    private String note; // 비고 (예: "1년 미만 월별 발생", "3년 근속 보너스")
    
    // Business methods
    public void useLeave(Double days) {
        if (this.remainingDays < days) {
            throw new IllegalStateException("사용 가능한 연차가 부족합니다.");
        }
        this.usedDays += days;
        this.remainingDays -= days;
    }
    
    public void cancelLeave(Double days) {
        this.usedDays -= days;
        this.remainingDays += days;
    }
    
    public void addLeave(Double days, String reason) {
        this.totalDays += days;
        this.remainingDays += days;
        this.note = (this.note != null ? this.note + "; " : "") + reason;
    }
}
