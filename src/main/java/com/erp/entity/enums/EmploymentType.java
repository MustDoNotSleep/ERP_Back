package com.erp.entity.enums;

// 16번 컬럼: employmentType (고용 형태)
public enum EmploymentType {
    REGULAR("정규직"), CONTRACT("계약직"), INTERN("인턴"), DISPATCH("파견직");

    private final String koreanName;
    EmploymentType(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
