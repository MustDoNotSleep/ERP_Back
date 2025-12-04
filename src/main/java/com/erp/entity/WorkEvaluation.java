package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "WorkEvaluations")

public class WorkEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    @Column(name = "evaluation_year", nullable = false)
    private Integer evaluationYear; // 연도

    @Column(name = "evaluation_quarter", nullable = false)
    private Integer evaluationQuarter; // 분기

    @Column(name = "attitude_score")
    private Integer attitudeScore; // 태도 점수

    @Column(name = "achievement_score")
    private Integer achievementScore; // 목표 달성 점수

    @Column(name = "collaboration_score")
    private Integer collaborationScore; // 협업 점수

    @Column(name = "contribution_grade", length = 2)
    private String contributionGrade; // 기여도 등급

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluatorId")
    private Employee evaluator; // 평가자

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getter/Setter 생략 (필요시 추가)
}
