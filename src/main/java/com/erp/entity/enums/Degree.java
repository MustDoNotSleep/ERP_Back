package com.erp.entity.enums;

// degree (학위/졸업 구분)
public enum Degree {
    HIGH_SCHOOL("고등학교 졸업"), JUNIOR_COLLEGE("전문학사"), BACHELOR("학사"), 
    MASTER("석사"), DOCTOR("박사"), ETC("기타");

    private final String koreanName;
    Degree(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
