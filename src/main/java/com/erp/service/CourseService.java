package com.erp.service;

import com.erp.dto.CourseDto;
import com.erp.entity.Course;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.CourseRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;

    public Page<CourseDto.Response> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(CourseDto.Response::from);
    }

    public CourseDto.Response getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course", id.toString()));
        return CourseDto.Response.from(course);
    }

    public List<CourseDto.Response> getCoursesByEmployeeId(Long employeeId) {
        // 직원 존재 여부 확인
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Employee", employeeId.toString());
        }
        
        return courseRepository.findCoursesByEmployeeId(employeeId).stream()
                .map(CourseDto.Response::from)
                .toList();
    }

    @Transactional
    public CourseDto.Response createCourse(CourseDto.Request request) {
        Employee creator = employeeRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getCreatorId().toString()));

        Course course = Course.builder()
                .courseName(request.getCourseName())
                .completionCriteria(request.getCompletionCriteria())
                .capacity(request.getCapacity())
                .courseType(request.getCourseType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .objective(request.getObjective())
                .creator(creator)
                .build();

        Course saved = courseRepository.save(course);
        return CourseDto.Response.from(saved);
    }

    @Transactional
    public CourseDto.Response updateCourse(Long id, CourseDto.UpdateRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course", id.toString()));

        courseRepository.delete(course);
        
        Course updated = Course.builder()
                .id(id)
                .courseName(request.getCourseName())
                .completionCriteria(request.getCompletionCriteria())
                .capacity(request.getCapacity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .objective(request.getObjective())
                .creator(course.getCreator())
                .courseType(course.getCourseType())
                .build();

        Course saved = courseRepository.save(updated);
        return CourseDto.Response.from(saved);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course", id.toString());
        }
        courseRepository.deleteById(id);
    }
}
