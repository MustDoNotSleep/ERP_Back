package com.erp.entity;

import com.erp.entity.enums.LeaveDuration;
import com.erp.entity.enums.LeaveStatus;
import com.erp.entity.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leaves")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Leave extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaveId")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveDuration duration; // 연차, 반차, 반반차
    
    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;
    
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedBy")
    private Employee approvedBy;
    
    private LocalDate approvedAt;
    
    // Business methods
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDate.now();
        this.status = LeaveStatus.APPROVED;
    }
    
    public void reject(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDate.now();
        this.status = LeaveStatus.REJECTED;
    }
    
    public void cancel() {
        this.status = LeaveStatus.CANCELLED;
    }
}