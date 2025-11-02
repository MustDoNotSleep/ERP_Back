package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// degree (학위/졸업 구분)
public enum Degree {
    HIGH_SCHOOL("고등학교 졸업"), 
    JUNIOR_COLLEGE("전문학사"), 
    BACHELOR("학사"), 
    MASTER("석사"), 
    DOCTOR("박사"), 
    ETC("기타");

    private final String koreanName;
    
    Degree(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static Degree fromKorean(String value) {
        // 영어 상수명으로 먼저 시도
        try {
            return Degree.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (Degree degree : Degree.values()) {
                if (degree.koreanName.equals(value)) {
                    return degree;
                }
            }
            throw new IllegalArgumentException("Unknown Degree: " + value);
        }
    }
}
