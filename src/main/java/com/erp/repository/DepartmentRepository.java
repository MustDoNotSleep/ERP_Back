package com.erp.repository;

import com.erp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    List<Department> findByParentDepartmentIsNull();
    Optional<Department> findByName(String name);
    List<Department> findByParentDepartment(Department parent);
}