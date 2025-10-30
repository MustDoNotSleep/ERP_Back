package com.erp.repository;

import com.erp.entity.DocumentApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.DocumentType;
import com.erp.entity.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentApplicationRepository extends JpaRepository<DocumentApplication, Long> {
    List<DocumentApplication> findByEmployee(Employee employee);
    List<DocumentApplication> findByDocumentStatus(DocumentStatus status);
    List<DocumentApplication> findByDocumentType(DocumentType documentType);
    List<DocumentApplication> findByEmployeeAndApplicationDateBetween(
        Employee employee, LocalDateTime start, LocalDateTime end);
}