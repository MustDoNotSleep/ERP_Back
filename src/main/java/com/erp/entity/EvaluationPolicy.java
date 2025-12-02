package com.erp.entity;

import com.erp.entity.enums.EvaluationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "EvaluationPolicy")
public class EvaluationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    // --- 1. 평가 시즌 정보 ---
    private String seasonName;
    private LocalDate startDate;
    private LocalDate endDate;

    // --- 2. 평가 설정 ---
    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    @Builder.Default
    private String evaluationSection = "부서별"; // 고정값

    // --- 3. 파일 정보 ---
    private String evaluationFormPath;
    private String originalFileName;

    // --- 4. 가중치 ---
    @Builder.Default
    private Integer performanceWeight = 70;

    @Builder.Default
    private Integer competencyWeight = 30;

    // --- 5. 타겟팅 (⭐ 문자열 → BIGINT FK 기반으로 변경) ---
    private Long targetDepartmentId;   // FK: departments.departmentId
    private Long targetPositionId;     // FK: positions.positionId

    @Builder.Default
    private String mappingMethod = "자동지정"; // 고정값

    // --- 6. 생성자 정보 ---
    private Long createdById;

    private LocalDateTime createdAt;

    // --- PrePersist 기본값 자동 설정 ---
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (evaluationSection == null) evaluationSection = "부서별";
        if (mappingMethod == null) mappingMethod = "자동지정";
        if (performanceWeight == null) performanceWeight = 70;
        if (competencyWeight == null) competencyWeight = 30;
    }
}
