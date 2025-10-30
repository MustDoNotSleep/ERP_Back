package com.erp.entity.enums;

// 5. Language (언어)
public enum DocumentLanguage {
    KOREAN("국문"), ENGLISH("영문");

    private final String koreanName;
    DocumentLanguage(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
