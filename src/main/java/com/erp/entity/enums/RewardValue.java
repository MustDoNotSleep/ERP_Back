package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RewardValue {
    // 프론트의 옵션과 매칭
    TEAM_CONTRIBUTION("팀 기여 우수"),
    CORE_TECH("핵심 기술 개발"),
    LONG_SERVICE("장기 근속"),
    ETC("기타");

    private final String description;

    RewardValue(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static RewardValue from(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("선택")) {
            return null;
        }
        try {
            return RewardValue.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (RewardValue val : RewardValue.values()) {
                if (val.description.equals(value)) {
                    return val;
                }
            }
            // 매칭되는 게 없으면 그냥 null 처리하거나 ETC로 보낼 수도 있음
            return null; 
        }
    }
}