package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.SalaryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryInfoRepository extends JpaRepository<SalaryInfo, String> {
    Optional<SalaryInfo> findByEmployee(Employee employee);
    void deleteByEmployee(Employee employee);
}