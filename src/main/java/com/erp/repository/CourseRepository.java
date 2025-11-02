package com.erp.repository;

import com.erp.entity.Course;
import com.erp.entity.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByCourseType(CourseType type);
    List<Course> findByStartDateGreaterThanEqual(LocalDate date);
    List<Course> findByCapacityGreaterThan(Integer capacity);
    
    // 특정 직원이 신청한 교육과정 조회
    @Query("SELECT c FROM Course c JOIN CourseApplication ca ON c.id = ca.course.id WHERE ca.employee.id = :employeeId")
    List<Course> findCoursesByEmployeeId(@Param("employeeId") Long employeeId);
}