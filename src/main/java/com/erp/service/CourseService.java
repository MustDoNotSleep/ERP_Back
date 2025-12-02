package com.erp.service;

import com.erp.dto.CourseDto;
import com.erp.entity.Course;
import com.erp.entity.Employee;
import com.erp.entity.enums.RequestStatus;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.CourseRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public Page<CourseDto.Response> searchCourses(String courseName, String status, String dateStatus, Pageable pageable) {
        Page<Course> courses = courseRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            // 교육명 검색 (LIKE)
            if (courseName != null && !courseName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("courseName"), "%" + courseName + "%"));
            }
            
            // 승인 상태 필터
            if (status != null && !status.trim().isEmpty()) {
                try {
                    RequestStatus requestStatus = RequestStatus.fromString(status);
                    predicates.add(criteriaBuilder.equal(root.get("status"), requestStatus));
                } catch (IllegalArgumentException e) {
                    // 잘못된 status 값은 무시
                }
            }
            
            // 교육 기간 상태 필터
            if (dateStatus != null && !dateStatus.trim().isEmpty()) {
                LocalDate today = LocalDate.now();
                switch (dateStatus.toUpperCase()) {
                    case "UPCOMING": // 예정
                        predicates.add(criteriaBuilder.greaterThan(root.get("startDate"), today));
                        break;
                    case "ONGOING": // 진행중
                        predicates.add(criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), today),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), today)
                        ));
                        break;
                    case "COMPLETED": // 완료
                        predicates.add(criteriaBuilder.lessThan(root.get("endDate"), today));
                        break;
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
        
        return courses.map(CourseDto.Response::from);
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
        // 현재 로그인한 사용자를 creator로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee creator = employeeRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Employee", authentication.getName()));

        Course course = Course.builder()
                .courseName(request.getCourseName())
                .completionCriteria(request.getCompletionCriteria())
                .capacity(request.getCapacity())
                .courseType(request.getCourseType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .objective(request.getObjective())
                .price(request.getPrice())
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
                .price(request.getPrice())
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

    @Transactional
    public CourseDto.Response approveCourse(Long id, CourseDto.ApprovalRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course", id.toString()));

        // 이미 처리된 과정은 수정 불가
        if (course.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 교육 과정입니다. 현재 상태: " + 
                course.getStatus().getKoreanName());
        }

        // 현재 로그인한 사용자를 승인자로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee approver = employeeRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Employee", authentication.getName()));

        courseRepository.delete(course);
        
        RequestStatus newStatus = request.isApproved() ? RequestStatus.APPROVED : RequestStatus.REJECTED;
        
        Course updated = Course.builder()
                .id(id)
                .courseName(course.getCourseName())
                .completionCriteria(course.getCompletionCriteria())
                .capacity(course.getCapacity())
                .courseType(course.getCourseType())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .objective(course.getObjective())
                .price(course.getPrice())
                .creator(course.getCreator())
                .status(newStatus)
                .approver(approver)
                .processedDate(LocalDateTime.now())
                .comment(request.getComment())
                .build();

        Course saved = courseRepository.save(updated);
        return CourseDto.Response.from(saved);
    }
}
