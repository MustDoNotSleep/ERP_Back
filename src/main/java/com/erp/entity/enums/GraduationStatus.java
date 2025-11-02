package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// graduationStatus (졸업 상태)
public enum GraduationStatus {
    GRADUATED("졸업"), 
    COMPLETED("수료"), 
    ATTENDING("재학"), 
    DROPPED("중퇴");

    private final String koreanName;
    
    GraduationStatus(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static GraduationStatus fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 영어 상수명으로 먼저 시도
        try {
            return GraduationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (GraduationStatus status : GraduationStatus.values()) {
                if (status.koreanName.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown GraduationStatus: " + value);
        }
    }
}
