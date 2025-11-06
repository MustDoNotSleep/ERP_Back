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
        private Integer copies; // 👈 1. (추가) 프론트 폼과 맞추기 위해 'copies' 추가
    }
    
    @Getter
    @Builder
    public static class Response {
        
        
        private Long documentId;
        private EmployeeDto employee;   
        
        private DocumentType documentType;
        private Integer copies;         
        
        private LocalDateTime issueDate;  
        
        private DocumentStatus documentStatus;
        private LocalDateTime applicationDate;

        private String purpose;
        private DocumentLanguage language;
        // private String reason; // (기존 DTO 필드 - 프론트에서 현재 미사용)
        private String processorName;
        // private LocalDateTime processedAt; // (기존 DTO 필드 - 'issueDate'로 대체됨)
        private String rejectionReason;
        // private List<String> issuedFiles; // (기존 DTO 필드 - 프론트에서 현재 미사용)

        
        // 👈 6. (추가) 프론트엔드에서 사용할 중첩 Employee 객체 DTO
        @Getter
        @Builder
        public static class EmployeeDto {
            private Long employeeId;
            private String name;
            // private String departmentName; // (부서명도 필요하다면 여기에 추가)
        }


        // 👈 7. from() 메소드 수정
        public static Response from(DocumentApplication application) {
            
            // (추가) EmployeeDto 객체 생성 로직
            EmployeeDto employeeDto = EmployeeDto.builder()
                .employeeId(application.getEmployee().getId()) // (DB의 employeeId 필드명)
                .name(application.getEmployee().getName())
                .build();
            
            // (기존) return Response.builder()
                return Response.builder()
                // .id(application.getId()) // (수정)
                    .documentId(application.getDocumentId()) // (수정)

                // .employeeName(application.getEmployee().getName()) // (수정)
                // .departmentName(application.getEmployee().getDepartment() != null ? // (수정)
                // application.getEmployee().getDepartment().getDepartmentName() : null) // (수정)
                    .employee(employeeDto) // (수정) 'employee' 객체로 대체

                    .documentType(application.getDocumentType())
                    .copies(application.getCopies()) // (추가) 'copies' 매핑

                // .documentType(entity.getDocumentType() != null ? entity.getDocumentType().getKoreanName() : null)
                // .documentStatus(entity.getDocumentStatus() != null ? entity.getDocumentStatus().getKoreanName() : null)
                    .purpose(application.getPurpose())
                    .language(application.getLanguage())
                    // .reason(application.getReason()) // (기존 DTO 필드 - 프론트에서 현재 미사용)
                    .documentStatus(application.getDocumentStatus())
                    .applicationDate(application.getApplicationDate())
                    .processorName(application.getProcessor() != null ? 
                            application.getProcessor().getName() : null)
                
                // .processedAt(application.getProcessedAt()) // (수정)
                    .issueDate(application.getProcessedAt()) // (수정) 'issueDate'로 대체

                    .rejectionReason(application.getRejectionReason())
                    // .issuedFiles(application.getIssuedFiles()) // (기존 DTO 필드 - 프론트에서 현재 미사용)
                    // .build(); // (기존)
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