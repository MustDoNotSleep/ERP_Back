package com.erp.repository;

import com.erp.entity.Course;
import com.erp.entity.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseType(CourseType type);
    List<Course> findByStartDateGreaterThanEqual(LocalDate date);
    List<Course> findByCapacityGreaterThan(Integer capacity);
}