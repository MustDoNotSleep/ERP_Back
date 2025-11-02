package com.erp.service;

import com.erp.dto.AppointmentRequestDto;
import com.erp.entity.AppointmentRequest;
import com.erp.entity.Department;
import com.erp.entity.Employee;
import com.erp.entity.Position;
import com.erp.entity.enums.RequestStatus;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.AppointmentRequestRepository;
import com.erp.repository.DepartmentRepository;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentRequestService {

    private final AppointmentRequestRepository appointmentRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public Page<AppointmentRequestDto.Response> getAllRequests(Pageable pageable) {
        return appointmentRequestRepository.findAll(pageable)
                .map(AppointmentRequestDto.Response::from);
    }

    public List<AppointmentRequestDto.Response> getRequestsByTargetEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return appointmentRequestRepository.findByTargetEmployee(employee).stream()
                .map(AppointmentRequestDto.Response::from)
                .collect(Collectors.toList());
    }

    public AppointmentRequestDto.Response getRequestById(Long id) {
        AppointmentRequest request = appointmentRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppointmentRequest", id.toString()));
        return AppointmentRequestDto.Response.from(request);
    }

    @Transactional
    public AppointmentRequestDto.Response createRequest(AppointmentRequestDto.Request request) {
        Employee targetEmployee = employeeRepository.findById(request.getTargetEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getTargetEmployeeId().toString()));
        
        // 현재 로그인한 사용자를 requestingEmployee로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Employee requestingEmployee = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Employee", currentUserEmail));

        Department newDepartment = null;
        if (request.getNewDepartmentId() != null) {
            newDepartment = departmentRepository.findById(request.getNewDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department", request.getNewDepartmentId().toString()));
        }
        
        Position newPosition = null;
        if (request.getNewPositionId() != null) {
            newPosition = positionRepository.findById(request.getNewPositionId())
                    .orElseThrow(() -> new EntityNotFoundException("Position", request.getNewPositionId().toString()));
        }

        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
                .targetEmployee(targetEmployee)
                .requestingEmployee(requestingEmployee)
                .appointmentType(request.getAppointmentType())
                .newDepartment(newDepartment)
                .newPosition(newPosition)
                .effectiveDate(request.getEffectiveDate())
                .reason(request.getReason())
                .status(RequestStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        AppointmentRequest saved = appointmentRequestRepository.save(appointmentRequest);
        return AppointmentRequestDto.Response.from(saved);
    }

    @Transactional
    public AppointmentRequestDto.Response approveOrReject(Long id, AppointmentRequestDto.ApprovalRequest request) {
        AppointmentRequest appointmentRequest = appointmentRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppointmentRequest", id.toString()));

        // 이미 처리된 요청은 수정 불가
        if (appointmentRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 인사발령 요청은 수정할 수 없습니다. 현재 상태: " + 
                appointmentRequest.getStatus().getKoreanName());
        }

        // 현재 로그인한 사용자를 승인자로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee approver = employeeRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Employee", authentication.getName()));

        appointmentRequestRepository.delete(appointmentRequest);
        
        AppointmentRequest updated = AppointmentRequest.builder()
                .id(id)
                .targetEmployee(appointmentRequest.getTargetEmployee())
                .requestingEmployee(appointmentRequest.getRequestingEmployee())
                .appointmentType(appointmentRequest.getAppointmentType())
                .newDepartment(appointmentRequest.getNewDepartment())
                .newPosition(appointmentRequest.getNewPosition())
                .effectiveDate(appointmentRequest.getEffectiveDate())
                .reason(appointmentRequest.getReason())
                .status(request.isApproved() ? RequestStatus.APPROVED : RequestStatus.REJECTED)
                .requestDate(appointmentRequest.getRequestDate())
                .approver(approver)
                .processedDate(LocalDateTime.now())
                .build();

        AppointmentRequest saved = appointmentRequestRepository.save(updated);
        return AppointmentRequestDto.Response.from(saved);
    }

    @Transactional
    public void deleteRequest(Long id) {
        if (!appointmentRequestRepository.existsById(id)) {
            throw new EntityNotFoundException("AppointmentRequest", id.toString());
        }
        appointmentRequestRepository.deleteById(id);
    }
}
