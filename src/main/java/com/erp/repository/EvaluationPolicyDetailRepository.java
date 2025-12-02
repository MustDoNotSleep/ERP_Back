package com.erp.repository;

import com.erp.entity.EvaluationPolicyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EvaluationPolicyDetailRepository extends JpaRepository<EvaluationPolicyDetail, Long> {
    long countByEvaluationPolicy_PolicyId(Long policyId);

   @Query("SELECT d FROM EvaluationPolicyDetail d " +
       "JOIN d.evaluationPolicy p " +
       "JOIN d.employee e " +
       "WHERE (:seasonName IS NULL OR :seasonName = '' OR p.seasonName = :seasonName) " +
       "AND (:deptId IS NULL OR e.department.id = :deptId) " +
       "AND (:posId IS NULL OR e.position.id = :posId)")
List<EvaluationPolicyDetail> findBySearchCriteria(
        @Param("seasonName") String seasonName,
        @Param("deptId") Long deptId,
        @Param("posId") Long posId
);
}