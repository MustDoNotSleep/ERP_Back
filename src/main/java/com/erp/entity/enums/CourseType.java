package com.erp.entity.enums;

// 5. CourseType (교육 과정 유형)
public enum CourseType {
    REQUIRED("필수이수"), ELECTIVE("선택이수");

    private final String koreanName;
    CourseType(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}