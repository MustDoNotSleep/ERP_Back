package com.erp.repository;

import com.erp.entity.DocumentApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.DocumentType;
import com.erp.entity.enums.DocumentStatus;
import org.springframework.data.domain.Page; // 👈 (추가)
import org.springframework.data.domain.Pageable; // 👈 (추가)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 👈 (추가)
import org.springframework.data.repository.query.Param; // 👈 (추가)
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // 👈 (추가)

@Repository
public interface DocumentApplicationRepository extends JpaRepository<DocumentApplication, Long> {
    
    // 🚨 (수정) N+1 문제 해결을 위해 'JOIN FETCH'를 사용하는 'findByEmployeeWithEmployee'로 대체

    /**
     * (Pageable 버전) getAllApplications용
     * Employee(신청자)와 Processor(처리자)를 한 번에 JOIN FETCH
     */
    // @Query("SELECT da FROM DocumentApplication da JOIN FETCH da.employee") // 👈 (수정 전)
    @Query(value = "SELECT da FROM DocumentApplication da JOIN FETCH da.employee e LEFT JOIN FETCH da.processor p",
           countQuery = "SELECT count(da) FROM DocumentApplication da") // (Pageable용 countQuery)
    Page<DocumentApplication> findAllWithEmployee(Pageable pageable);

    /**
     * (List 버전) getApplicationsByEmployeeId용
     * Employee(신청자)와 Processor(처리자)를 한 번에 JOIN FETCH
     */
    // @Query("SELECT da FROM DocumentApplication da JOIN FETCH da.employee WHERE da.employee = :employee") // 👈 (수정 전)
    @Query("SELECT da FROM DocumentApplication da JOIN FETCH da.employee e LEFT JOIN FETCH da.processor p WHERE da.employee = :employee")
    List<DocumentApplication> findByEmployeeWithEmployee(@Param("employee") Employee employee);

    /**
     * (Optional 버전) getApplicationById / approveOrReject용
     * Employee(신청자)와 Processor(처리자)를 한 번에 JOIN FETCH
     */
    // @Query("SELECT da FROM DocumentApplication da JOIN FETCH da.employee WHERE da.documentId = :id") // 👈 (수정 전)
    @Query("SELECT da FROM DocumentApplication da JOIN FETCH da.employee e LEFT JOIN FETCH da.processor p WHERE da.documentId = :id")
    Optional<DocumentApplication> findByIdWithEmployee(@Param("id") Long id);
    // --- (수정 안 함) 기존의 다른 쿼리 메소드들 ---
    List<DocumentApplication> findByDocumentStatus(DocumentStatus status);
    List<DocumentApplication> findByDocumentType(DocumentType documentType);
    List<DocumentApplication> findByEmployeeAndApplicationDateBetween(
        Employee employee, LocalDateTime start, LocalDateTime end);
}