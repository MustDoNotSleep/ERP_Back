package com.erp.service;

import com.erp.dto.ResignationApplicationDto;
import com.erp.entity.Employee;
import com.erp.entity.ResignationApplication;
import com.erp.entity.enums.ApplicationStatus;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.ResignationApplicationRepository;
import com.erp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 퇴직 신청 서비스
 * - 퇴직 신청 생성
 * - 퇴직 신청 승인/반려
 * - 퇴직 신청 조회 (전체, 직원별, 상태별)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResignationApplicationService {
    
    private final ResignationApplicationRepository resignationApplicationRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * 전체 퇴직 신청 조회 (페이징)
     */
    public Page<ResignationApplicationDto.Response> getAllApplications(Pageable pageable) {
        return resignationApplicationRepository.findAllWithDetails(pageable)
                .map(ResignationApplicationDto.Response::from);
    }
    
    /**
     * 특정 직원의 퇴직 신청 조회
     */
    public List<ResignationApplicationDto.Response> getApplicationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return resignationApplicationRepository.findByEmployeeWithDetails(employee).stream()
                .map(ResignationApplicationDto.Response::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 퇴직 신청 상세 조회
     */
    public ResignationApplicationDto.Response getApplicationById(Long id) {
        ResignationApplication application = resignationApplicationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("ResignationApplication", id.toString()));
        
        return ResignationApplicationDto.Response.from(application);
    }
    
    /**
     * 상태별 퇴직 신청 조회
     */
    public List<ResignationApplicationDto.Response> getApplicationsByStatus(ApplicationStatus status) {
        return resignationApplicationRepository.findByStatusWithDetails(status).stream()
                .map(ResignationApplicationDto.Response::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 퇴직 신청 생성
     * employeeId가 없으면 토큰에서 자동으로 가져옴
     */
    @Transactional
    public ResignationApplicationDto.Response createApplication(ResignationApplicationDto.Request request) {
        // employeeId가 없으면 현재 로그인한 사용자의 ID 사용
        final Long employeeId = request.getEmployeeId() != null 
            ? request.getEmployeeId() 
            : SecurityUtil.getCurrentEmployeeId();
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        // 이미 퇴사 처리된 직원인지 확인
        if (employee.getQuitDate() != null) {
            throw new IllegalStateException("이미 퇴사 처리된 직원입니다.");
        }
        
        // 대기 중인 퇴직 신청이 있는지 확인
        List<ResignationApplication> existingApplications = resignationApplicationRepository.findByEmployee(employee);
        boolean hasPending = existingApplications.stream()
                .anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING);
        
        if (hasPending) {
            throw new IllegalStateException("이미 처리 대기 중인 퇴직 신청이 있습니다.");
        }
        
        ResignationApplication application = ResignationApplication.builder()
                .employee(employee)
                .desiredResignationDate(request.getDesiredResignationDate())
                .reason(request.getReason())
                .detailedReason(request.getDetailedReason())
                .status(ApplicationStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();
        
        ResignationApplication saved = resignationApplicationRepository.save(application);
        return ResignationApplicationDto.Response.from(saved);
    }
    
    /**
     * 퇴직 신청 승인/반려
     * processorId가 없으면 토큰에서 자동으로 가져옴
     */
    @Transactional
    public ResignationApplicationDto.Response approveOrReject(Long id, ResignationApplicationDto.ApprovalRequest request) {
        ResignationApplication application = resignationApplicationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("ResignationApplication", id.toString()));
        
        // processorId가 없으면 현재 로그인한 사용자의 ID 사용
        final Long processorId = request.getProcessorId() != null
            ? request.getProcessorId()
            : SecurityUtil.getCurrentEmployeeId();
        
        Employee processor = employeeRepository.findById(processorId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", processorId.toString()));
        
        // 이미 처리된 신청인지 확인
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        
        // 반려 시 반려 사유 필수 확인
        if (!request.getApproved() && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new IllegalArgumentException("반려 시 반려 사유는 필수입니다.");
        }
        
        // 승인/반려 처리
        application.processApplication(
                processor,
                request.getApproved(),
                request.getRejectionReason(),
                request.getFinalResignationDate()
        );
        
        // 변경 사항은 @Transactional에 의해 자동으로 flush됨
        return ResignationApplicationDto.Response.from(application);
    }
    
    /**
     * 퇴직 신청 취소 (신청자만 가능, PENDING 상태만)
     */
    @Transactional
    public ResignationApplicationDto.Response cancelApplication(Long id) {
        ResignationApplication application = resignationApplicationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("ResignationApplication", id.toString()));
        
        application.cancel();
        return ResignationApplicationDto.Response.from(application);
    }
    
    /**
     * 퇴직 신청 삭제 (관리자만)
     */
    @Transactional
    public void deleteApplication(Long id) {
        if (!resignationApplicationRepository.existsById(id)) {
            throw new EntityNotFoundException("ResignationApplication", id.toString());
        }
        resignationApplicationRepository.deleteById(id);
    }
    
    /**
     * 퇴직 신청 통계 조회
     */
    public ResignationApplicationDto.Statistics getStatistics() {
        Long total = resignationApplicationRepository.count();
        Long pending = resignationApplicationRepository.countByStatus(ApplicationStatus.PENDING);
        Long approved = resignationApplicationRepository.countByStatus(ApplicationStatus.APPROVED);
        Long rejected = resignationApplicationRepository.countByStatus(ApplicationStatus.REJECTED);
        
        return ResignationApplicationDto.Statistics.builder()
                .totalApplications(total)
                .pendingApplications(pending)
                .approvedApplications(approved)
                .rejectedApplications(rejected)
                .build();
    }
}
