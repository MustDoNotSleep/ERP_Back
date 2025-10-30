package com.erp.entity.enums;

// graduationStatus (졸업 상태)
public enum GraduationStatus {
    GRADUATED("졸업"), COMPLETED("수료"), ATTENDING("재학"), DROPPED("중퇴");

    private final String koreanName;
    GraduationStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
