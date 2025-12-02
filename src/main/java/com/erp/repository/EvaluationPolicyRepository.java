package com.erp.repository;

import com.erp.entity.EvaluationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluationPolicyRepository extends JpaRepository<EvaluationPolicy, Long> {
    List<EvaluationPolicy> findAllByOrderByPolicyIdDesc();
    
}