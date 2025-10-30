package com.erp.entity.enums;

// 5. Status (신청 상태)
public enum ApplicationStatus {
    PENDING("대기"), APPROVED("승인"), REJECTED("반려");

    private final String koreanName;
    ApplicationStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}