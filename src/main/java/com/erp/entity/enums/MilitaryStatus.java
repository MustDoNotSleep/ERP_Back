package com.erp.entity.enums;

// 병역 상태 (militaryStatus)
public enum MilitaryStatus {
    HYEON_YEOK("현역"), MIBOK("미복"), HYEON_YEOK_EOP_SEUM("현역;면제받았음"), ETC("기타");

    private final String koreanName;
    MilitaryStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
