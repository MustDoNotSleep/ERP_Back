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
