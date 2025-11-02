package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 면제 사유 (exemptionReason)
public enum ExemptionReason {
    BOG_MU_DAE_GI("복무대기"), 
    SANG_YE_GYEONG("생계곤란"), 
    JIL_BYEONG("질병"), 
    ETC("기타");

    private final String koreanName;
    
    ExemptionReason(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static ExemptionReason fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ExemptionReason.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (ExemptionReason reason : ExemptionReason.values()) {
                if (reason.koreanName.equals(value)) {
                    return reason;
                }
            }
            throw new IllegalArgumentException("Unknown ExemptionReason: " + value);
        }
    }
}
