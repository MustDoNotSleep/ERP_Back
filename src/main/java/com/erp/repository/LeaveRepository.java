package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.Leave;
import com.erp.entity.enums.LeaveStatus;
import com.erp.entity.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    // 특정 직원의 모든 휴가 조회
    List<Leave> findByEmployeeOrderByStartDateDesc(Employee employee);
    
    // 특정 직원의 상태별 휴가 조회
    List<Leave> findByEmployeeAndStatusOrderByStartDateDesc(Employee employee, LeaveStatus status);
    
    // 특정 직원의 휴가 종류별 조회
    List<Leave> findByEmployeeAndTypeOrderByStartDateDesc(Employee employee, LeaveType type);
    
    // 상태별 휴가 조회 (관리자용)
    List<Leave> findByStatusOrderByStartDateDesc(LeaveStatus status);
    
    // 기간별 휴가 조회
    @Query("SELECT l FROM Leave l WHERE " +
           "(l.startDate BETWEEN :startDate AND :endDate) OR " +
           "(l.endDate BETWEEN :startDate AND :endDate) OR " +
           "(l.startDate <= :startDate AND l.endDate >= :endDate) " +
           "ORDER BY l.startDate DESC")
    List<Leave> findByPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // 특정 직원의 승인된 휴가 기간 조회
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND l.status = 'APPROVED' " +
           "AND ((l.startDate BETWEEN :startDate AND :endDate) OR " +
           "(l.endDate BETWEEN :startDate AND :endDate) OR " +
           "(l.startDate <= :startDate AND l.endDate >= :endDate)) " +
           "ORDER BY l.startDate DESC")
    List<Leave> findApprovedLeavesInPeriod(
        @Param("employee") Employee employee,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // 특정 직원의 연도별 휴가 사용 일수 계산
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee " +
           "AND l.status = 'APPROVED' " +
           "AND YEAR(l.startDate) = :year " +
           "ORDER BY l.startDate DESC")
    List<Leave> findApprovedLeavesByEmployeeAndYear(
        @Param("employee") Employee employee,
        @Param("year") int year
    );
}
