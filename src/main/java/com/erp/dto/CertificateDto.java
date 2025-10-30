package com.erp.dto;

import com.erp.entity.Certificate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class CertificateDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private String certificateName;
        private String issuingAuthority;
        private LocalDate expirationDate;
        private LocalDate acquisitionDate;
        private String score;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private String certificateName;
        private String issuingAuthority;
        private LocalDate expirationDate;
        private LocalDate acquisitionDate;
        private String score;
        
        public static Response from(Certificate certificate) {
            return Response.builder()
                .id(certificate.getId())
                .employeeName(certificate.getEmployee().getName())
                .certificateName(certificate.getCertificateName())
                .issuingAuthority(certificate.getIssuingAuthority())
                .expirationDate(certificate.getExpirationDate())
                .acquisitionDate(certificate.getAcquisitionDate())
                .score(certificate.getScore())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private String certificateName;
        private String issuingAuthority;
        private LocalDate expirationDate;
        private LocalDate acquisitionDate;
        private String score;
    }
}
