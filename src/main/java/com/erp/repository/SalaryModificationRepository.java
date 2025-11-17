package com.erp.repository;

import com.erp.entity.SalaryModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface SalaryModificationRepository extends JpaRepository<SalaryModification, Long> {
    List<SalaryModification> findByPaymentDateOrderByModifiedAtDesc(YearMonth paymentDate);
}
