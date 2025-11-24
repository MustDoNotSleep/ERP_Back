package com.erp.entity.enums;

/**
 * 복리후생 거래 타입
 */
public enum WelfareTransactionType {
    GRANT("지급"),      // 회사에서 복리후생비 지급
    USE("사용");        // 직원이 복리후생비 사용

    private final String description;

    WelfareTransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
