package com.erp.entity;

import com.erp.entity.enums.Document;
import jakarta.persistence.*;
import lombok.*;
import main.java.com.erp.entity.enums.DocumentLanguage;
import main.java.com.erp.entity.enums.DocumentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DocumentApplications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DocumentApplication extends BaseEntity{
    
    // 1. documentId (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "documentId")
    private Long id;
    
    // 2. employeeId (외래 키: 신청한 직원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee; 

    // 3. documentType (Enum 적용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    // 4. purpose (신청 목적)
    @Column(length = 100)
    private String purpose;

    // 5. language (Enum 적용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentLanguage language;
    
    // 6. reason (TEXT 타입이므로 String으로 매핑)
    @Lob 
    private String reason;

    // 7. status (Enum 적용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus documentStatus;
    
    // 8. applicationDate (신청 일시)
    private LocalDateTime applicationDate;

    // 9. processedBy (외래 키: 처리한 직원/관리자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processedBy")
    private Employee processor;

    // 10. processedAt (처리 일시)
    private LocalDateTime processedAt;

    // 11. rejectionReason (반려 사유)
    @Column(length = 500)
    private String rejectionReason;
    
    // 12. issuedFiles (발급된 파일 경로/이름 목록)
    @ElementCollection
    @CollectionTable(
        name = "document_issued_files",
        joinColumns = @JoinColumn(name = "documentId")
    )
    @Column(name = "file_url", length = 255)
    @Builder.Default
    private List<String> issuedFiles = new ArrayList<>();
    
    /*
     * 비즈니스 로직 추가 영역 (예: 상태 업데이트)
     */
}