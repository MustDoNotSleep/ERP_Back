package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RewardType {
    CONTRIBUTION("공로상"),
    BEST_EMPLOYEE("우수사원상"),
    SPECIAL("특별포상"),
    LONG_SERVICE("장기근속상");

    private final String description;

    RewardType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static RewardType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RewardType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (RewardType type : RewardType.values()) {
                if (type.description.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown RewardType: " + value);
        }
    }
}