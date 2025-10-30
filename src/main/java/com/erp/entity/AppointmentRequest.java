package com.erp.entity;

import com.erp.entity.enums.Appointment;
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

    // 6. effectiveStartDate (발령 시작일)
    private LocalDate effectiveStartDate;

    // 7. effectiveEndDate (발령 종료일 - 휴직, 파견 등의 경우 사용)
    private LocalDate effectiveEndDate;

    // 8. reason (TEXT 타입이므로 String으로 매핑)
    @Lob // 데이터베이스에서 TEXT 타입으로 매핑되도록 힌트를 줌
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

    /*
     * 비즈니스 로직 추가 영역 (예: 상태 변경, 승인/반려 메서드)
     */
}