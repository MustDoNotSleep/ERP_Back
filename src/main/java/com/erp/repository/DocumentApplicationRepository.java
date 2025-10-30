package com.erp.repository;

import com.erp.entity.DocumentApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.entity.enums.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentApplicationRepository extends JpaRepository<DocumentApplication, String> {
    List<DocumentApplication> findByEmployee(Employee employee);
    List<DocumentApplication> findByStatus(ApplicationStatus status);
    List<DocumentApplication> findByDocumentType(Document documentType);
    List<DocumentApplication> findByEmployeeAndCreatedAtBetween(
        Employee employee, LocalDateTime start, LocalDateTime end);
}