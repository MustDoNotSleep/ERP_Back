package com.erp.entity.enums;

public enum WelfareType {
    EDUCATION("교육 지원금"),
    BOOK("도서 구입비"),
    OTHER("기타");

    private final String description;

    WelfareType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
