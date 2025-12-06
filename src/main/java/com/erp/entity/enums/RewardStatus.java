package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RewardStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    RewardStatus(String description) { 
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static RewardStatus from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RewardStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (RewardStatus status : RewardStatus.values()) {
                if (status.description.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown RewardStatus: " + value);
        }
    }
}