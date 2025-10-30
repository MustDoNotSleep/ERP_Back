package com.erp.entity.enums;

// 7. Status (신청 상태)
public enum DocumentStatus {
    PENDING("승인대기"), APPROVED("승인완료"), REJECTED("승인반려");

    private final String koreanName;
    DocumentStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
