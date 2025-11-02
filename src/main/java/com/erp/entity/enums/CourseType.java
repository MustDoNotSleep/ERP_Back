package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 5. CourseType (교육 과정 유형)
public enum CourseType {
    REQUIRED("필수이수"), 
    ELECTIVE("선택이수");

    private final String koreanName;
    
    CourseType(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static CourseType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // 영어 constant 이름으로 매칭
        for (CourseType type : CourseType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        // 한글 이름으로 매칭
        for (CourseType type : CourseType.values()) {
            if (type.koreanName.equals(value)) {
                return type;
            }
        }
        
        // "optional" -> ELECTIVE 매핑 추가
        if ("optional".equalsIgnoreCase(value) || "선택".equals(value)) {
            return ELECTIVE;
        }
        
        // "required" -> REQUIRED 매핑
        if ("mandatory".equalsIgnoreCase(value) || "필수".equals(value)) {
            return REQUIRED;
        }
        
        throw new IllegalArgumentException("Invalid CourseType: " + value + 
            ". Allowed values: REQUIRED(필수이수), ELECTIVE(선택이수), required, optional");
    }
}