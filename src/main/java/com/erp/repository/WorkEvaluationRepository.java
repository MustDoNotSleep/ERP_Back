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

    Page<WorkEvaluation> findAllByEvaluationYearAndEvaluationQuarterAndEmployeeIn(
        Integer evaluationYear, 
        Integer evaluationQuarter, 
        List<Employee> employees, 
        Pageable pageable
    );
}