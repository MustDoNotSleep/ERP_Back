package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 19번 컬럼: nationality (국적)
public enum Nationality {
    DOMESTIC("내국인"), FOREIGN("외국인");

    private final String koreanName;
    Nationality(String koreanName) { this.koreanName = koreanName; }
    
    @JsonValue  // JSON 직렬화 시 이 값 사용
    public String getKoreanName() { return koreanName; }
    
    @JsonCreator  // JSON 역직렬화 시 한글/영어 → Enum 변환
    public static Nationality fromKorean(String value) {
        // 먼저 Enum 상수명(영어)으로 시도
        for (Nationality nat : Nationality.values()) {
            if (nat.name().equals(value)) {
                return nat;
            }
        }
        // 그 다음 한글로 시도
        for (Nationality nat : Nationality.values()) {
            if (nat.koreanName.equals(value)) {
                return nat;
            }
        }
        throw new IllegalArgumentException("Unknown Nationality: " + value);
    }
}
