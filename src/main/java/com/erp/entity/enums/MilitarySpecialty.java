package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 특기/주특기 (militarySpecialty)
public enum MilitarySpecialty {
    BOB_YUNG("보병"), 
    PO_BYEONG("포병"), 
    TONG_SIN("통신"), 
    GONG_BYEONG("공병"), 
    ETC("기타");

    private final String koreanName;
    
    MilitarySpecialty(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static MilitarySpecialty fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return MilitarySpecialty.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (MilitarySpecialty specialty : MilitarySpecialty.values()) {
                if (specialty.koreanName.equals(value)) {
                    return specialty;
                }
            }
            throw new IllegalArgumentException("Unknown MilitarySpecialty: " + value);
        }
    }
}
