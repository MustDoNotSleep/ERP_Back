package com.erp.entity;

import com.erp.entity.enums.AppointmentType;
import com.erp.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AppointmentRequests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    
    // 1. appointmentRequestId (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointmentRequestId")
    private Long id;
    
    // 2. targetEmployeeId (발령 대상 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetEmployeeId", nullable = false)
    private Employee targetEmployee; 

    // 3. requestingEmployeeId (요청한 직원/관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestingEmployeeId", nullable = false)
    private Employee requestingEmployee;

    // 4. appointmentType (Enum 적용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType appointmentType;

    // 5. newDepartmentId (발령 후 소속될 새 부서)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newDepartmentId") // Nullable, 전보(TRANSFER)가 아닐 경우 필요 없을 수 있음
    private Department newDepartment;
    
    // 5-1. newPositionId (발령 후 새 직급)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newPositionId") // Nullable, 승진/강등이 아닐 경우 필요 없을 수 있음
    private Position newPosition;

    // 6. effectiveDate (발령 일자)
    private LocalDate effectiveDate;

    // 8. reason (TEXT 타입이므로 String으로 매핑)
    @Lob // 데이터베이스에서 TEXT 타입으로 매핑되도록 힌트를 줌
    @Column(columnDefinition = "TEXT")
    private String reason;

    // 9. status (Enum 적용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    // 10. approverId (최종 승인/반려한 직원/관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approverId") // 승인 전에는 Null일 수 있음
    private Employee approver;

    // 11. requestDate (요청 일시)
    private LocalDateTime requestDate;

    // 12. processedDate (처리 완료 일시 - 승인/반려된 시점)
    private LocalDateTime processedDate;
    
    // 13. isApplied (발령 적용 여부 - 스케줄러가 실제로 적용했는지 표시)
    @Column(nullable = false)
    @Builder.Default
    private Boolean isApplied = false;
    
    // 14. appliedDate (발령 적용 일시 - 스케줄러가 실제로 적용한 시점)
    private LocalDateTime appliedDate;

    /*
     * 비즈니스 로직 추가 영역 (예: 상태 변경, 승인/반려 메서드)
     */
    
    public void markAsApplied() {
        this.isApplied = true;
        this.appliedDate = LocalDateTime.now();
    }
}