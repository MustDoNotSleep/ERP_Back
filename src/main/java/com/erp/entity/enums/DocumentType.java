package com.erp.entity.enums;

// 3. DocumentType (문서 종류)
public enum DocumentType {
    CERTIFICATE_OF_EMPLOYMENT("재직증명서"), CERTIFICATE_OF_CAREER("경력증명서"), 
    CERTIFICATE_OF_INCOME("근로소득증명서");

    private final String koreanName;
    DocumentType(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
