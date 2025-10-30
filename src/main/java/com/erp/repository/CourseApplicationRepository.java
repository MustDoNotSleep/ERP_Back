package com.erp.repository;

import com.erp.entity.CourseApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseApplicationRepository extends JpaRepository<CourseApplication, String> {
    List<CourseApplication> findByEmployee(Employee employee);
    List<CourseApplication> findByStatus(ApplicationStatus status);
    List<CourseApplication> findByCourseId(String courseId);
    Long countByCourseIdAndStatus(String courseId, ApplicationStatus status);
}