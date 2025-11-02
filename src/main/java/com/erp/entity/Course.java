package com.erp.entity;

import com.erp.entity.enums.CourseType;
import com.erp.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Course extends BaseEntity{
    
    // 1. courseId (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courseId")
    private Long id;
    
    // 2. courseName (교육 과정 이름)
    @Column(length = 255, nullable = false)
    private String courseName;
    
    // 3. completionCriteria (수료 기준)
    @Column(length = 255)
    private String completionCriteria;
    
    // 4. capacity (수용 인원)
    private Integer capacity;
    
    // 5. courseType (Enum 적용: 필수이수, 선택이수)
    @Enumerated(EnumType.STRING)
    @Column(name = "courseType", nullable = false)
    private CourseType courseType;
    
    // 6. startDate (시작일)
    private LocalDate startDate;
    
    // 7. endDate (종료일)
    private LocalDate endDate;
    
    // 8. objective (TEXT 타입이므로 String으로 매핑)
    @Lob // 데이터베이스에서 TEXT 타입으로 매핑되도록 힌트를 줌
    private String objective; // 교육 목표
    
    // 9. createdBy (외래 키: 과정을 생성한 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", nullable = false)
    private Employee creator; // Employee 엔티티를 참조한다고 가정

    // 10. status (교육 과정 승인 상태)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    // 11. approver (승인/반려 처리한 관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver")
    private Employee approver;

    // 12. processedDate (승인/반려 처리 일시)
    private LocalDateTime processedDate;

    // 13. comment (승인/반려 코멘트)
    @Column(length = 500)
    private String comment;

    /*
     * 비즈니스 로직 추가 영역 
     */
}