package com.erp.entity.enums;

// 9. Status (요청 상태)
public enum RequestStatus {
    PENDING("대기"), APPROVED("최종승인"), REJECTED("반려");

    private final String koreanName;
    RequestStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
