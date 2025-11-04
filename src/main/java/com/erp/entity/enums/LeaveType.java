package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveType {
    ANNUAL("연차"),
    SICK("병가"),
    MATERNITY("출산휴가"),
    BEREAVEMENT("조의");

    private final String koreanName;

    LeaveType(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static LeaveType fromString(String value) {
        if (value == null) {
            return null;
        }

        // 한글로 입력된 경우
        for (LeaveType type : LeaveType.values()) {
            if (type.koreanName.equals(value)) {
                return type;
            }
        }

        // 영문으로 입력된 경우
        try {
            return LeaveType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid LeaveType: " + value);
        }
    }
}
