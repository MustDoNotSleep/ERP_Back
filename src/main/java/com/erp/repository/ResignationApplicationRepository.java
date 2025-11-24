package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.ResignationApplication;
import com.erp.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResignationApplicationRepository extends JpaRepository<ResignationApplication, Long> {
    
    // 기본 조회
    List<ResignationApplication> findByEmployee(Employee employee);
    List<ResignationApplication> findByStatus(ApplicationStatus status);
    
    // N+1 방지를 위한 JOIN FETCH
    @Query("SELECT ra FROM ResignationApplication ra " +
           "JOIN FETCH ra.employee e " +
           "LEFT JOIN FETCH e.department d " +
           "LEFT JOIN FETCH e.position p " +
           "LEFT JOIN FETCH ra.processor proc")
    Page<ResignationApplication> findAllWithDetails(Pageable pageable);
    
    @Query("SELECT ra FROM ResignationApplication ra " +
           "JOIN FETCH ra.employee e " +
           "LEFT JOIN FETCH e.department d " +
           "LEFT JOIN FETCH e.position p " +
           "LEFT JOIN FETCH ra.processor proc " +
           "WHERE ra.employee = :employee")
    List<ResignationApplication> findByEmployeeWithDetails(@Param("employee") Employee employee);
    
    @Query("SELECT ra FROM ResignationApplication ra " +
           "JOIN FETCH ra.employee e " +
           "LEFT JOIN FETCH e.department d " +
           "LEFT JOIN FETCH e.position p " +
           "LEFT JOIN FETCH ra.processor proc " +
           "WHERE ra.id = :id")
    Optional<ResignationApplication> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT ra FROM ResignationApplication ra " +
           "JOIN FETCH ra.employee e " +
           "LEFT JOIN FETCH e.department d " +
           "LEFT JOIN FETCH e.position p " +
           "LEFT JOIN FETCH ra.processor proc " +
           "WHERE ra.status = :status")
    List<ResignationApplication> findByStatusWithDetails(@Param("status") ApplicationStatus status);
    
    // 기간별 조회
    List<ResignationApplication> findByDesiredResignationDateBetween(LocalDate startDate, LocalDate endDate);
    List<ResignationApplication> findByApplicationDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    // 상태별 카운트
    Long countByStatus(ApplicationStatus status);
    Long countByEmployee(Employee employee);
}
