package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByEmployee(Employee employee);
    List<WorkExperience> findByEmployeeOrderByStartDateDesc(Employee employee);
}
