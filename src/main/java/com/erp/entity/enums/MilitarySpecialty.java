package com.erp.entity.enums;

// 특기/주특기 (militarySpecialty)
public enum MilitarySpecialty {
    BOB_YUNG("보병"), PO_BYEONG("포병"), TONG_SIN("통신"), GONG_BYEONG("공병"), ETC("기타");

    private final String koreanName;
    MilitarySpecialty(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
