package com.erp.repository;

import com.erp.entity.Leave;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, String> {
    List<Leave> findByEmployee(Employee employee);
    List<Leave> findByEmployeeAndStatus(Employee employee, Leave.LeaveStatus status);
    
    List<Leave> findByStartDateBetweenOrEndDateBetween(
        LocalDate startDate1, LocalDate endDate1,
        LocalDate startDate2, LocalDate endDate2
    );
    
    @Query("SELECT l FROM Leave l WHERE l.employee = ?1 AND l.status = 'APPROVED' " +
           "AND ((l.startDate BETWEEN ?2 AND ?3) OR (l.endDate BETWEEN ?2 AND ?3))")
    List<Leave> findApprovedLeavesInPeriod(Employee employee, LocalDate startDate, LocalDate endDate);
    
    List<Leave> findByStatusAndStartDateGreaterThanEqual(Leave.LeaveStatus status, LocalDate date);
}