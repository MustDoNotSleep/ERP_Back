package com.erp.entity;

import com.erp.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 퇴직 신청 엔티티
 * - 직원이 퇴직을 신청하고, 관리자가 승인/반려를 처리
 * - 승인 시 Employee의 quitDate가 자동으로 업데이트됨
 */
@Entity
@Table(name = "resignation_applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResignationApplication extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resignationId")
    private Long id;
    
    // 신청자 (퇴직 희망 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;
    
    // 퇴직 희망일
    @Column(nullable = false)
    private LocalDate desiredResignationDate;
    
    // 퇴직 사유
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;
    
    // 상세 사유 (추가 정보)
    @Column(columnDefinition = "TEXT")
    private String detailedReason;
    
    // 신청 상태 (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    // 신청 일시
    @Column(nullable = false)
    private LocalDateTime applicationDate;
    
    // 처리자 (승인/반려한 관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processedBy")
    private Employee processor;
    
    // 처리 일시
    private LocalDateTime processedAt;
    
    // 반려 사유
    @Column(length = 500)
    private String rejectionReason;
    
    // 최종 퇴사일 (승인 시 결정되며, Employee.quitDate와 동기화)
    private LocalDate finalResignationDate;
    
    /**
     * 퇴직 신청 승인/반려 처리
     * 
     * @param processor 처리자 (관리자)
     * @param isApproved 승인 여부 (true: 승인, false: 반려)
     * @param rejectionReason 반려 사유 (반려 시 필수)
     * @param finalResignationDate 최종 퇴사일 (승인 시, null이면 희망일 사용)
     */
    public void processApplication(
            Employee processor, 
            boolean isApproved, 
            String rejectionReason,
            LocalDate finalResignationDate) {
        
        this.processor = processor;
        this.processedAt = LocalDateTime.now();
        this.status = isApproved ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED;
        
        if (isApproved) {
            // 승인 시: 최종 퇴사일 설정 (null이면 희망일 사용)
            this.finalResignationDate = (finalResignationDate != null) 
                    ? finalResignationDate 
                    : this.desiredResignationDate;
            this.rejectionReason = null;
            
            // Employee의 quitDate 업데이트
            this.employee.updateQuitDate(this.finalResignationDate);
        } else {
            // 반려 시: 반려 사유 저장
            this.rejectionReason = rejectionReason;
            this.finalResignationDate = null;
        }
    }
    
    /**
     * 신청 취소 (PENDING 상태만 가능)
     */
    public void cancel() {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 신청만 취소할 수 있습니다.");
        }
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = "신청자가 취소함";
        this.processedAt = LocalDateTime.now();
    }
}
