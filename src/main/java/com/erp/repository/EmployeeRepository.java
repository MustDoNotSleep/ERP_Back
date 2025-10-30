package com.erp.repository;

import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByPasswordStartingWith(String prefix);
    List<Employee> findByRrnStartingWith(String prefix);
}