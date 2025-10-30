package com.erp.entity.enums;

// 4. AppointmentType (인사 발령 유형)
public enum AppointmentType {
    TRANSFER("전보"), PROMOTION("승진"), REINSTATEMENT("복직"), 
    DISPATCH("파견"), POSITION_CHANGE("직무 변경"), POSITION_NAME("직책 임명"), 
    POSITION_CANCELLATION("직책 해임"), RESTRICTION("승진 제한"), LEAVE("휴직 요청");

    private final String koreanName;
    AppointmentType(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 9. Status (요청 상태)
public enum RequestStatus {
    PENDING("대기"), APPROVED("최종승인"), REJECTED("반려");

    private final String koreanName;
    RequestStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}