package com.erp.repository;

import com.erp.entity.Course;
import com.erp.entity.CourseApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {
    List<CourseApplication> findByEmployee(Employee employee);
    List<CourseApplication> findByStatus(ApplicationStatus status);
    List<CourseApplication> findByCourse(Course course);
    Long countByCourseAndStatus(Course course, ApplicationStatus status);
}