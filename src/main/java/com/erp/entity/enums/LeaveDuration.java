package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveDuration {
    FULL_DAY("종일"),                  // 1일 (8시간)
    HALF_DAY_AM("오전반차"),           // 0.5일 (오전 4시간)
    HALF_DAY_PM("오후반차"),           // 0.5일 (오후 4시간)
    QUARTER_DAY_AM("오전반반차"),      // 0.25일 (오전 2시간)
    QUARTER_DAY_PM("오후반반차");      // 0.25일 (오후 2시간)

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
            case HALF_DAY_AM:
            case HALF_DAY_PM:
                return 0.5;
            case QUARTER_DAY_AM:
            case QUARTER_DAY_PM:
                return 0.25;
            default:
                return 0.0;
        }
    }
    
    // 반차/반반차 여부
    public boolean isHalfDay() {
        return this == HALF_DAY_AM || this == HALF_DAY_PM;
    }
    
    public boolean isQuarterDay() {
        return this == QUARTER_DAY_AM || this == QUARTER_DAY_PM;
    }
    
    // 오전/오후 구분
    public boolean isAM() {
        return this == HALF_DAY_AM || this == QUARTER_DAY_AM;
    }
    
    public boolean isPM() {
        return this == HALF_DAY_PM || this == QUARTER_DAY_PM;
    }
}

