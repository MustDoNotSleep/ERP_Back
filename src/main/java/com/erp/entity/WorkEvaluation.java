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
    private Integer evaluationYear;

    @Column(name = "evaluation_quarter", nullable = false)
    private Integer evaluationQuarter;

    @Column(name = "attitude_score")
    private Integer attitudeScore;

    @Column(name = "achievement_score")
    private Integer achievementScore;

    @Column(name = "collaboration_score")
    private Integer collaborationScore;

    @Column(name = "contribution_grade", length = 2)
    private String contributionGrade;

    @Column(name = "total_grade", length = 2)
    private String totalGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.임시저장;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id")
    private Employee evaluator;

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

    public enum Status {
        임시저장, 제출완료
    }

    // Getter/Setter 생략 (필요시 추가)
}
