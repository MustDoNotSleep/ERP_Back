package com.erp.entity;

import com.erp.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CourseApplications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CourseApplication {
    
    // 1. applicationId (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicationId")
    private Long id;
    
    // 2. courseId (외래 키: 신청한 교육 과정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", nullable = false)
    private Course course; // Course 엔티티를 참조한다고 가정

    // 3. employeeId (외래 키: 신청한 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee; 

    // 4. applicationDate (신청 일시)
    private LocalDateTime applicationDate;
    
    // 5. status (Enum 적용: 대기, 승인, 반려)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    
    // 6. processedBy (외래 키: 처리한 직원/관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processedBy") // 처리 전에는 Null일 수 있음
    private Employee processor;

    // 7. processedAt (처리 일시)
    private LocalDateTime processedAt;

    // 8. rejectionReason (반려 사유)
    @Column(length = 500)
    private String rejectionReason;

    /*
     * 비즈니스 로직 추가 영역 (예: 신청 상태 업데이트 메서드)
     */
}