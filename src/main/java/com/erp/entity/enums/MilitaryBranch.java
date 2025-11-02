package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 군별 (militaryBranch)
public enum MilitaryBranch {
    YUK_GOON("육군"), 
    HAE_GOON("해군"), 
    GONG_GOON("공군"), 
    HAEDAE("해병대"), 
    ETC("기타");

    private final String koreanName;
    
    MilitaryBranch(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static MilitaryBranch fromKorean(String value) {
        // 빈 문자열이나 null이면 null 반환 (선택사항)
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 영어 상수명으로 먼저 시도
        try {
            return MilitaryBranch.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (MilitaryBranch branch : MilitaryBranch.values()) {
                if (branch.koreanName.equals(value)) {
                    return branch;
                }
            }
            throw new IllegalArgumentException("Unknown MilitaryBranch: " + value);
        }
    }
}
