package com.erp.repository;

import com.erp.entity.Department;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartment(Department department);
    List<Employee> findByNameContaining(String name);
    
    // Security migration methods
    List<Employee> findByPasswordStartingWith(String prefix);
    List<Employee> findByRrnStartingWith(String prefix);
}