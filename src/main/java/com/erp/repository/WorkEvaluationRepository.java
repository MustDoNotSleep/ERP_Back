package com.erp.repository;

import com.erp.entity.WorkEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkEvaluationRepository extends JpaRepository<WorkEvaluation, Long> {
    
    // 직원의 분기별 평가 단건 조회
    Optional<WorkEvaluation> findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(
        Long employeeId, 
        Integer evaluationYear, 
        Integer evaluationQuarter
    );

    // 부서의 분기별 평가 목록 조회 (ID 기준)
    List<WorkEvaluation> findByEmployee_Department_IdAndEvaluationYearAndEvaluationQuarter(
        Long departmentId, 
        Integer evaluationYear, 
        Integer evaluationQuarter
    );

    // 직원의 전체 평가 내역 조회
    List<WorkEvaluation> findByEmployee_IdOrderByEvaluationYearDescEvaluationQuarterDesc(
        Long employeeId
    );

    // 특정 연도의 부서 평가 내역 조회 (ID 기준)
    List<WorkEvaluation> findByEmployee_Department_IdAndEvaluationYear(
        Long departmentId, 
        Integer evaluationYear
    );

    List<WorkEvaluation> findByEvaluationYear(Integer evaluationYear);

    List<WorkEvaluation> findByEvaluationYearAndEvaluationQuarter(Integer evaluationYear, Integer evaluationQuarter);

    // ✅ [수정 1] DepartmentName -> TeamName 으로 변경
    // (Department 엔티티 안에 private String teamName; 변수가 있어야 함)
    List<WorkEvaluation> findByEmployee_Department_TeamName(String teamName);

    // ✅ [수정 2] DepartmentName -> TeamName 으로 변경
    List<WorkEvaluation> findByEmployee_Department_TeamNameAndEvaluationYear(String teamName, Integer evaluationYear);

    // ✅ [수정 3] DepartmentName -> TeamName 으로 변경
    List<WorkEvaluation> findByEmployee_Department_TeamNameAndEvaluationYearAndEvaluationQuarter(
        String teamName,
        Integer evaluationYear,
        Integer evaluationQuarter
    );
}