package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.WorkEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkEvaluationRepository extends JpaRepository<WorkEvaluation, Long> {
    // 직원의 분기별 평가 단건 조회
    java.util.Optional<WorkEvaluation> findByEmployee_IdAndEvaluationYearAndEvaluationQuarter(Long employeeId, Integer evaluationYear, Integer evaluationQuarter);

    // 부서의 분기별 평가 목록 조회
    java.util.List<WorkEvaluation> findByEmployee_Department_IdAndEvaluationYearAndEvaluationQuarter(Long departmentId, Integer evaluationYear, Integer evaluationQuarter);

    Page<WorkEvaluation> findAllByEvaluationYearAndEvaluationQuarterAndEmployeeIn(
        Integer evaluationYear, 
        Integer evaluationQuarter, 
        List<Employee> employees, 
        Pageable pageable
    );
}