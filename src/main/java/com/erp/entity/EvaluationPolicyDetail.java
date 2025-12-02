package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "EvaluationPolicyDetail")
public class EvaluationPolicyDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    // --- 1. 정책과의 관계 (N:1) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policyId", nullable = false)
    private EvaluationPolicy evaluationPolicy;

    // --- 2. [수정됨] 직원과의 관계 (N:1) ---
    // 기존: private Long employeeId; 
    // 수정: Employee 객체와 직접 연결하여 외래키(FK) 관계를 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false) 
    private Employee employee;

    // --- 3. 스냅샷 정보 (평가 당시의 이름/부서) ---
    private String employeeName;  
    private String teamName;      

    // --- 4. 결과 데이터 ---
    private Double finalScore;
    private String finalGrade;
}