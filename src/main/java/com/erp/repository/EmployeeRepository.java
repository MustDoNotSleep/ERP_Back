package com.erp.repository;

import com.erp.entity.Department;
import com.erp.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartment(Department department);
    List<Employee> findByNameContaining(String name);
    
    // 검색 기능 (이름 또는 이메일)
    Page<Employee> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);
    
    // Security migration methods
    List<Employee> findByPasswordStartingWith(String prefix);
    List<Employee> findByRrnStartingWith(String prefix);
}