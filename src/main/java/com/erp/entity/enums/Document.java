package com.erp.entity.enums;

// 3. DocumentType (문서 종류)
public enum DocumentType {
    CERTIFICATE_OF_EMPLOYMENT("재직증명서"), CERTIFICATE_OF_CAREER("경력증명서"), 
    CERTIFICATE_OF_INCOME("근로소득증명서");

    private final String koreanName;
    DocumentType(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 5. Language (언어)
public enum DocumentLanguage {
    KOREAN("국문"), ENGLISH("영문");

    private final String koreanName;
    DocumentLanguage(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 7. Status (신청 상태)
public enum DocumentStatus {
    PENDING("승인대기"), APPROVED("승인완료"), REJECTED("승인반려");

    private final String koreanName;
    DocumentStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}