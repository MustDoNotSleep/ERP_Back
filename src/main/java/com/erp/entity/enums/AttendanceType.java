package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceType {
    NORMAL("정상출근"),
    LATE("지각"),
    EARLY_LEAVE("조퇴"),
    ABSENT("결근"),
    LEAVE("휴가"),
    REMOTE("재택근무"),
    OVERTIME("야근"),
    WEEKEND_WORK("주말근무"),
    HOLIDAY_WORK("휴일근무");

    private final String koreanName;

    AttendanceType(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static AttendanceType fromString(String value) {
        if (value == null) {
            return null;
        }

        // 한글로 입력된 경우
        for (AttendanceType type : AttendanceType.values()) {
            if (type.koreanName.equals(value)) {
                return type;
            }
        }

        // 영문으로 입력된 경우
        try {
            return AttendanceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AttendanceType: " + value);
        }
    }
}
