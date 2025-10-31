package com.erp.service;

import com.erp.dto.CourseApplicationDto;
import com.erp.entity.Course;
import com.erp.entity.CourseApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.CourseApplicationRepository;
import com.erp.repository.CourseRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseApplicationService {

    private final CourseApplicationRepository courseApplicationRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;

    public Page<CourseApplicationDto.Response> getAllApplications(Pageable pageable) {
        return courseApplicationRepository.findAll(pageable)
                .map(CourseApplicationDto.Response::from);
    }

    public List<CourseApplicationDto.Response> getApplicationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return courseApplicationRepository.findByEmployee(employee).stream()
                .map(CourseApplicationDto.Response::from)
                .collect(Collectors.toList());
    }

    public CourseApplicationDto.Response getApplicationById(Long id) {
        CourseApplication application = courseApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseApplication", id.toString()));
        return CourseApplicationDto.Response.from(application);
    }

    @Transactional
    public CourseApplicationDto.Response createApplication(CourseApplicationDto.Request request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course", request.getCourseId().toString()));
        
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getEmployeeId().toString()));

        CourseApplication application = CourseApplication.builder()
                .course(course)
                .employee(employee)
                .applicationDate(LocalDateTime.now())
                .status(ApplicationStatus.PENDING)
                .build();

        CourseApplication saved = courseApplicationRepository.save(application);
        return CourseApplicationDto.Response.from(saved);
    }

    @Transactional
    public CourseApplicationDto.Response approveOrReject(Long id, CourseApplicationDto.ApprovalRequest request) {
        CourseApplication application = courseApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseApplication", id.toString()));

        Employee processor = employeeRepository.findById(request.getProcessorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getProcessorId().toString()));

        courseApplicationRepository.delete(application);
        
        CourseApplication updated = CourseApplication.builder()
                .id(id)
                .course(application.getCourse())
                .employee(application.getEmployee())
                .applicationDate(application.getApplicationDate())
                .status(request.isApproved() ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED)
                .processor(processor)
                .processedAt(LocalDateTime.now())
                .rejectionReason(request.getRejectionReason())
                .build();

        CourseApplication saved = courseApplicationRepository.save(updated);
        return CourseApplicationDto.Response.from(saved);
    }

    @Transactional
    public void deleteApplication(Long id) {
        if (!courseApplicationRepository.existsById(id)) {
            throw new EntityNotFoundException("CourseApplication", id.toString());
        }
        courseApplicationRepository.deleteById(id);
    }
}
