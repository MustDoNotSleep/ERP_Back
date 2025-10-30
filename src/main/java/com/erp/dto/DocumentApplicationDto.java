package com.erp.dto;

import com.erp.entity.DocumentApplication;
import com.erp.entity.enums.DocumentType;
import com.erp.entity.enums.DocumentLanguage;
import com.erp.entity.enums.DocumentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentApplicationDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private DocumentType documentType;
        private String purpose;
        private DocumentLanguage language;
        private String reason;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private String departmentName;
        private DocumentType documentType;
        private String purpose;
        private DocumentLanguage language;
        private String reason;
        private DocumentStatus documentStatus;
        private LocalDateTime applicationDate;
        private String processorName;
        private LocalDateTime processedAt;
        private String rejectionReason;
        private List<String> issuedFiles;
        
        public static Response from(DocumentApplication application) {
            return Response.builder()
                .id(application.getId())
                .employeeName(application.getEmployee().getName())
                .departmentName(application.getEmployee().getDepartment() != null ?
                    application.getEmployee().getDepartment().getDepartmentName() : null)
                .documentType(application.getDocumentType())
                .purpose(application.getPurpose())
                .language(application.getLanguage())
                .reason(application.getReason())
                .documentStatus(application.getDocumentStatus())
                .applicationDate(application.getApplicationDate())
                .processorName(application.getProcessor() != null ? 
                    application.getProcessor().getName() : null)
                .processedAt(application.getProcessedAt())
                .rejectionReason(application.getRejectionReason())
                .issuedFiles(application.getIssuedFiles())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class ApprovalRequest {
        private Long processorId;
        private boolean approved;
        private String rejectionReason;
        private List<String> issuedFiles;
    }
}
