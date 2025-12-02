package com.erp.service;

import com.erp.dto.CourseApplicationDto;
import com.erp.entity.Course;
import com.erp.entity.CourseApplication;
import com.erp.entity.Employee;
import com.erp.entity.Welfare;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.entity.enums.WelfareTransactionType;
import com.erp.entity.enums.WelfareType;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.CourseApplicationRepository;
import com.erp.repository.CourseRepository;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.WelfareRepository;
import com.erp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseApplicationService {

    private final CourseApplicationRepository courseApplicationRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;
    private final WelfareRepository welfareRepository;
    
    // 연간 복리후생 예산
    private static final BigDecimal ANNUAL_WELFARE_BUDGET = new BigDecimal("2000000"); // 200만원

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

    public List<CourseApplicationDto.Response> getApplicationsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course", courseId.toString()));
        
        return courseApplicationRepository.findByCourse(course).stream()
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

        // processorId가 없으면 토큰에서 자동 추출
        Long processorId = request.getProcessorId();
        if (processorId == null) {
            processorId = SecurityUtil.getCurrentEmployeeId();
        }
        
        final Long finalProcessorId = processorId;
        Employee processor = employeeRepository.findById(finalProcessorId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", finalProcessorId.toString()));

        // 승인 시 복리후생 예산 확인 및 차감
        if (request.isApproved()) {
            Course course = application.getCourse();
            BigDecimal coursePrice = course.getPrice();
            
            if (coursePrice != null && coursePrice.compareTo(BigDecimal.ZERO) > 0) {
                // 신청자의 연간 복리후생 사용액 조회
                Employee applicant = application.getEmployee();
                String currentYear = String.valueOf(YearMonth.now().getYear());
                
                BigDecimal totalUsed = welfareRepository.getTotalUsedAmountByEmployeeAndYear(
                    applicant.getId(), 
                    currentYear
                );
                
                BigDecimal availableAmount = ANNUAL_WELFARE_BUDGET.subtract(totalUsed);
                
                // 예산 초과 체크
                if (coursePrice.compareTo(availableAmount) > 0) {
                    throw new IllegalStateException(
                        String.format("복리후생 예산이 부족하여 승인할 수 없습니다. (필요: %s원, 잔액: %s원)", 
                            coursePrice, availableAmount)
                    );
                }
                
                // Welfare 레코드 생성 (복리후생 비용 차감)
                Welfare welfare = Welfare.builder()
                    .employee(applicant)
                    .transactionType(WelfareTransactionType.USE)
                    .welfareType(WelfareType.EDUCATION)
                    .paymentMonth(YearMonth.now())
                    .amount(coursePrice)
                    .paymentDate(java.time.LocalDate.now())
                    .note(course.getCourseName() + " 교육비")
                    .approver(processor)
                    .isApproved(true) // 교육 승인 시 자동으로 복리후생도 승인
                    .build();
                
                welfareRepository.save(welfare);
            }
        }

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
