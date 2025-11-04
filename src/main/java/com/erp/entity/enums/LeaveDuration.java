package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveDuration {
    FULL_DAY("연차"),           // 1일 (8시간)
    HALF_DAY("반차"),           // 0.5일 (4시간)
    QUARTER_DAY("반반차");      // 0.25일 (2시간)

    private final String koreanName;

    LeaveDuration(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static LeaveDuration fromString(String value) {
        if (value == null) {
            return null;
        }

        // 한글로 입력된 경우
        for (LeaveDuration duration : LeaveDuration.values()) {
            if (duration.koreanName.equals(value)) {
                return duration;
            }
        }

        // 영문으로 입력된 경우
        try {
            return LeaveDuration.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid LeaveDuration: " + value);
        }
    }

    // 휴가 일수 반환
    public double getDays() {
        switch (this) {
            case FULL_DAY:
                return 1.0;
            case HALF_DAY:
                return 0.5;
            case QUARTER_DAY:
                return 0.25;
            default:
                return 0.0;
        }
    }
}
