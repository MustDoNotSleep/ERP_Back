package com.erp.service;

import com.erp.dto.DocumentApplicationDto;
import com.erp.entity.DocumentApplication;
import com.erp.entity.Employee;
import com.erp.entity.enums.DocumentStatus;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.DocumentApplicationRepository;
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
public class DocumentApplicationService {

    private final DocumentApplicationRepository documentApplicationRepository;
    private final EmployeeRepository employeeRepository;

    public Page<DocumentApplicationDto.Response> getAllApplications(Pageable pageable) {
        // 🚨 (수정) N+1 문제 해결을 위해 JOIN FETCH 쿼리가 필요합니다.
        // (Repository에 'findAllWithEmployee' 메소드 추가가 필요합니다 - 이전 답변 참고)
        
        // return documentApplicationRepository.findAll(pageable) // 👈 (수정 전)
        //         .map(DocumentApplicationDto.Response::from);    // 👈 (수정 전)
        return documentApplicationRepository.findAllWithEmployee(pageable) // 👈 (수정 후)
                .map(DocumentApplicationDto.Response::from);
    }

    public List<DocumentApplicationDto.Response> getApplicationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        // 🚨 (수정) N+1 문제 해결을 위해 JOIN FETCH 쿼리가 필요합니다.
        // (Repository에 'findByEmployeeWithEmployee' 메소드 추가가 필요합니다)
        
        // return documentApplicationRepository.findByEmployee(employee).stream() // 👈 (수정 전)
        //         .map(DocumentApplicationDto.Response::from)                   // 👈 (수정 전)
        //         .collect(Collectors.toList());                                // 👈 (수정 전)
        return documentApplicationRepository.findByEmployeeWithEmployee(employee).stream() // 👈 (수정 후)
                .map(DocumentApplicationDto.Response::from)
                .collect(Collectors.toList());
    }

    public DocumentApplicationDto.Response getApplicationById(Long id) {
        // 🚨 (수정) N+1 문제 해결을 위해 JOIN FETCH 쿼리가 필요합니다.
        // (Repository에 'findByIdWithEmployee' 메소드 추가가 필요합니다)
        
        // DocumentApplication application = documentApplicationRepository.findById(id) // 👈 (수정 전)
        //         .orElseThrow(() -> new EntityNotFoundException("DocumentApplication", id.toString())); // 👈 (수정 전)
        DocumentApplication application = documentApplicationRepository.findByIdWithEmployee(id) // 👈 (수정 후)
                .orElseThrow(() -> new EntityNotFoundException("DocumentApplication", id.toString()));
        return DocumentApplicationDto.Response.from(application);
    }

    @Transactional
    public DocumentApplicationDto.Response createApplication(DocumentApplicationDto.Request request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getEmployeeId().toString()));

        DocumentApplication application = DocumentApplication.builder()
                .employee(employee)
                .documentType(request.getDocumentType())
                .purpose(request.getPurpose())
                .language(request.getLanguage())
                .reason(request.getReason())
                .copies(request.getCopies()) // 👈 (수정) 주석 해제 및 DTO의 'copies' 필드 반영
                .documentStatus(DocumentStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();

        DocumentApplication saved = documentApplicationRepository.save(application);
        return DocumentApplicationDto.Response.from(saved);
    }

    @Transactional
    public DocumentApplicationDto.Response approveOrReject(Long id, DocumentApplicationDto.ApprovalRequest request) {
        // 🚨 (수정) N+1 방지를 위해 JOIN FETCH 사용
        // DocumentApplication application = documentApplicationRepository.findById(id) // 👈 (수정 전)
        //         .orElseThrow(() -> new EntityNotFoundException("DocumentApplication", id.toString())); // 👈 (수정 전)
        DocumentApplication application = documentApplicationRepository.findByIdWithEmployee(id) // 👈 (수정 후)
                .orElseThrow(() -> new EntityNotFoundException("DocumentApplication", id.toString()));

        Employee processor = employeeRepository.findById(request.getProcessorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getProcessorId().toString()));

        // --- 🚨 (수정) 업데이트 방식 변경 ---
        // 'delete' 후 'save'하는 비효율적인 방식 대신
        // 엔티티의 비즈니스 메소드를 호출하여 'Dirty Checking'으로 자동 UPDATE 되도록 변경
        
        // documentApplicationRepository.delete(application); // 👈 (수정 전)
        
        /* (수정 전: Builder로 새 객체를 만드는 방식)
        DocumentApplication updated = DocumentApplication.builder()
                .id(id)
                .employee(application.getEmployee())
                .documentType(application.getDocumentType())
                .purpose(application.getPurpose())
                .language(application.getLanguage())
                .reason(application.getReason())
                .documentStatus(request.isApproved() ? DocumentStatus.APPROVED : DocumentStatus.REJECTED)
                .applicationDate(application.getApplicationDate())
                .processor(processor)
                .processedAt(LocalDateTime.now())
                .rejectionReason(request.getRejectionReason())
                .issuedFiles(request.getIssuedFiles())
                .build();
        DocumentApplication saved = documentApplicationRepository.save(updated);
        */
        
        // 👈 (수정 후) 엔티티에 추가한 'processApplication' 메소드 호출
        application.processApplication(
            processor, 
            request.isApproved(), 
            request.getRejectionReason(), 
            request.getIssuedFiles()
        );

        // return DocumentApplicationDto.Response.from(saved); // 👈 (수정 전)
        return DocumentApplicationDto.Response.from(application); // 👈 (수정 후)
    }

    @Transactional
    public void deleteApplication(Long id) {
        if (!documentApplicationRepository.existsById(id)) {
            throw new EntityNotFoundException("DocumentApplication", id.toString());
        }
        documentApplicationRepository.deleteById(id);
    }
}