package com.erp.repository;

import com.erp.entity.AnnualLeaveBalance;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualLeaveBalanceRepository extends JpaRepository<AnnualLeaveBalance, Long> {
    
    Optional<AnnualLeaveBalance> findByEmployeeAndYear(Employee employee, Integer year);
    
    List<AnnualLeaveBalance> findByEmployee(Employee employee);
    
    List<AnnualLeaveBalance> findByYear(Integer year);
    
    List<AnnualLeaveBalance> findByEmployeeOrderByYearDesc(Employee employee);
}
