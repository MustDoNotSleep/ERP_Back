package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 4. AppointmentType (인사 발령 유형)
public enum AppointmentType {
    TRANSFER("전보"), 
    PROMOTION("승진"), 
    REINSTATEMENT("복직"), 
    DISPATCH("파견"), 
    POSITION_CHANGE("직무 변경"), 
    POSITION_NAME("직책 임명"), 
    POSITION_CANCELLATION("직책 해임"), 
    RESTRICTION("승진 제한"), 
    LEAVE("휴직 요청");

    private final String koreanName;
    
    AppointmentType(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static AppointmentType fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 영어 상수명으로 먼저 시도
        try {
            return AppointmentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (AppointmentType type : AppointmentType.values()) {
                if (type.koreanName.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown AppointmentType: " + value);
        }
    }
}
