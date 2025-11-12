package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 병역 상태 (militaryStatus)
public enum MilitaryStatus {
    HYEON_YEOK("현역"),
    GUN_PIL("군필"),
    MI_PIL("미필"),
    MYEON_JE("면제"),
    NOT_APPLICABLE("해당 없음");

    private final String koreanName;
    
    MilitaryStatus(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static MilitaryStatus fromKorean(String value) {
        // 영어 상수명으로 먼저 시도
        try {
            return MilitaryStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (MilitaryStatus status : MilitaryStatus.values()) {
                if (status.koreanName.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown MilitaryStatus: " + value);
        }
    }
}
