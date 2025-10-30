package com.erp.entity.enums;

// 19번 컬럼: nationality (국적)
public enum Nationality {
    DOMESTIC("내국인"), FOREIGN("외국인");

    private final String koreanName;
    Nationality(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
