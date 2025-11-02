package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 16번 컬럼: employmentType (고용 형태)
public enum EmploymentType {
    REGULAR("정규직"), CONTRACT("계약직"), INTERN("인턴"), DISPATCH("파견직");

    private final String koreanName;
    EmploymentType(String koreanName) { this.koreanName = koreanName; }
    
    @JsonValue  // JSON 직렬화 시 이 값 사용
    public String getKoreanName() { return koreanName; }
    
    @JsonCreator  // JSON 역직렬화 시 한글/영어 → Enum 변환
    public static EmploymentType fromKorean(String value) {
        // 먼저 Enum 상수명(영어)으로 시도
        for (EmploymentType type : EmploymentType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        // 그 다음 한글로 시도
        for (EmploymentType type : EmploymentType.values()) {
            if (type.koreanName.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EmploymentType: " + value);
    }
}
