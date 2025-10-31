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
        return documentApplicationRepository.findAll(pageable)
                .map(DocumentApplicationDto.Response::from);
    }

    public List<DocumentApplicationDto.Response> getApplicationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return documentApplicationRepository.findByEmployee(employee).stream()
                .map(DocumentApplicationDto.Response::from)
                .collect(Collectors.toList());
    }

    public DocumentApplicationDto.Response getApplicationById(Long id) {
        DocumentApplication application = documentApplicationRepository.findById(id)
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
                .documentStatus(DocumentStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();

        DocumentApplication saved = documentApplicationRepository.save(application);
        return DocumentApplicationDto.Response.from(saved);
    }

    @Transactional
    public DocumentApplicationDto.Response approveOrReject(Long id, DocumentApplicationDto.ApprovalRequest request) {
        DocumentApplication application = documentApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocumentApplication", id.toString()));

        Employee processor = employeeRepository.findById(request.getProcessorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee", request.getProcessorId().toString()));

        documentApplicationRepository.delete(application);
        
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
        return DocumentApplicationDto.Response.from(saved);
    }

    @Transactional
    public void deleteApplication(Long id) {
        if (!documentApplicationRepository.existsById(id)) {
            throw new EntityNotFoundException("DocumentApplication", id.toString());
        }
        documentApplicationRepository.deleteById(id);
    }
}
