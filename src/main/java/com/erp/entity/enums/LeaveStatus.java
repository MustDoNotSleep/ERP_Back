package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려"),
    CANCELLED("취소");

    private final String koreanName;

    LeaveStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static LeaveStatus fromString(String value) {
        if (value == null) {
            return null;
        }

        // 한글로 입력된 경우
        for (LeaveStatus status : LeaveStatus.values()) {
            if (status.koreanName.equals(value)) {
                return status;
            }
        }

        // 영문으로 입력된 경우
        try {
            return LeaveStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid LeaveStatus: " + value);
        }
    }
}
