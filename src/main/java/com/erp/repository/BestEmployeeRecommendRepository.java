package com.erp.repository;

import com.erp.dto.RecommendRequest;
import com.erp.entity.WorkEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BestEmployeeRecommendRepository extends JpaRepository<WorkEvaluation, Long> {
    /**
     * 평가 연도, 분기, 부서 기준으로 평가 데이터 조회
     */
    @Query("SELECT w FROM WorkEvaluation w WHERE w.evaluationYear = :year AND w.evaluationQuarter = :quarter " +
            "AND (:departmentId IS NULL OR w.employee.department.id = :departmentId)")
    List<WorkEvaluation> findByRecommendRequest(@Param("year") Integer year,
                                                @Param("quarter") Integer quarter,
                                                @Param("departmentId") Long departmentId);
}
