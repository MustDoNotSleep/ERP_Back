package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RewardItem {
    MONEY("상금"),
    POINT("포인트"),
    VACATION("연차"),
    PLAQUE("상패/감사장"),
    GIFT("상품권");

    private final String description;

    RewardItem(String description) {
        this.description = description;
    }

    // JSON으로 변환될 때(Serialize) 이 값이 나갑니다. (예: "상금")
    @JsonValue
    public String getDescription() {
        return description;
    }

    // JSON에서 값을 받을 때(Deserialize) 이 메서드가 실행됩니다.
    // "MONEY"라고 보내도 되고, "상금"이라고 보내도 알아서 찾아줍니다.
    @JsonCreator
    public static RewardItem from(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("선택")) {
            return null;
        }
        // 1. 영어 상수명으로 먼저 시도 (예: "MONEY")
        try {
            return RewardItem.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 2. 한국어 설명으로 검색 (예: "상금")
            for (RewardItem item : RewardItem.values()) {
                if (item.description.equals(value)) {
                    return item;
                }
            }
            // 3. 둘 다 없으면 에러
            throw new IllegalArgumentException("Unknown RewardItem: " + value);
        }
    }
}