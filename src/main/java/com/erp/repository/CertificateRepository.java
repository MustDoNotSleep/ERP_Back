package com.erp.repository;

import com.erp.entity.Certificate;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByEmployee(Employee employee);
    List<Certificate> findByEmployeeAndExpirationDateAfter(Employee employee, LocalDate date);
    List<Certificate> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);
}