package com.erp.service;

import com.erp.dto.CertificateDto;
import com.erp.entity.Certificate;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.CertificateRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EmployeeRepository employeeRepository;

    public List<CertificateDto.Response> getCertificatesByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return certificateRepository.findByEmployee(employee).stream()
                .map(CertificateDto.Response::from)
                .collect(Collectors.toList());
    }

    public CertificateDto.Response getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate", id.toString()));
        return CertificateDto.Response.from(certificate);
    }

    @Transactional
    public CertificateDto.Response createCertificate(Long employeeId, CertificateDto.Request request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));

        Certificate certificate = Certificate.builder()
                .employee(employee)
                .certificateName(request.getCertificateName())
                .issuingAuthority(request.getIssuingAuthority())
                .expirationDate(request.getExpirationDate())
                .acquisitionDate(request.getAcquisitionDate())
                .score(request.getScore())
                .build();

        Certificate saved = certificateRepository.save(certificate);
        return CertificateDto.Response.from(saved);
    }

    @Transactional
    public CertificateDto.Response updateCertificate(Long id, CertificateDto.UpdateRequest request) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate", id.toString()));

        certificateRepository.delete(certificate);
        
        Certificate updated = Certificate.builder()
                .id(id)
                .employee(certificate.getEmployee())
                .certificateName(request.getCertificateName())
                .issuingAuthority(request.getIssuingAuthority())
                .expirationDate(request.getExpirationDate())
                .acquisitionDate(request.getAcquisitionDate())
                .score(request.getScore())
                .build();

        Certificate saved = certificateRepository.save(updated);
        return CertificateDto.Response.from(saved);
    }

    @Transactional
    public void deleteCertificate(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new EntityNotFoundException("Certificate", id.toString());
        }
        certificateRepository.deleteById(id);
    }
}
