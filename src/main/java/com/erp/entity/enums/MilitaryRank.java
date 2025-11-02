package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 계급 (militaryRank)
public enum MilitaryRank {
    BYEONG_JANG("병장"), 
    SANG_BYEONG("상병"), 
    IL_BYEONG("일병"), 
    HA_SA("하사"), 
    ETC("기타");

    private final String koreanName;
    
    MilitaryRank(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static MilitaryRank fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return MilitaryRank.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (MilitaryRank rank : MilitaryRank.values()) {
                if (rank.koreanName.equals(value)) {
                    return rank;
                }
            }
            throw new IllegalArgumentException("Unknown MilitaryRank: " + value);
        }
    }
}
