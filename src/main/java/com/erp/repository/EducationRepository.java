package com.erp.repository;

import com.erp.entity.Education;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByEmployee(Employee employee);
    List<Education> findByEmployeeOrderByAdmissionDateDesc(Employee employee);
}