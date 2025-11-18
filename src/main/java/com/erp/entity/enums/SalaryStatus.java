package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SalaryStatus {
    DRAFT("초안"),
    CONFIRMED("확정"),
    PAID("지급완료");

    private final String koreanName;

    SalaryStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static SalaryStatus fromKorean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        // 영어 상수명으로 먼저 시도
        try {
            return SalaryStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 한국어 이름으로 검색
            for (SalaryStatus status : SalaryStatus.values()) {
                if (status.koreanName.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown SalaryStatus: " + value);
        }
    }
}
