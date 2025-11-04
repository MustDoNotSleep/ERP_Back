package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveType {
    // 연차 관련
    ANNUAL("연차", true, true),           // 유급 연차
    
    // 병가 관련
    SICK("병가", false, false),           // 무급 병가
    SICK_PAID("유급병가", true, false),    // 유급 병가 (회사 복리후생)
    
    // 출산/육아 관련
    MATERNITY("출산휴가", true, false),    // 유급 출산휴가 (90일)
    PATERNITY("배우자출산휴가", true, false), // 유급 배우자 출산휴가 (10일)
    CHILDCARE("육아휴직", false, false),   // 무급 육아휴직
    
    // 경조사 관련
    MARRIAGE("결혼휴가", true, false),      // 유급 결혼휴가 (본인 5일)
    FAMILY_MARRIAGE("가족결혼휴가", true, false), // 유급 가족 결혼 (1일)
    BEREAVEMENT("경조사", true, false),  // 유급 조의휴가 (부모 5일, 조부모/배우자부모 3일, 형제자매 1일)
    
    // 기타
    OFFICIAL("공가", true, false),        // 유급 공가 (병역, 선거 등)
    UNPAID("무급휴가", false, false);     // 무급 휴가

    private final String koreanName;
    private final boolean paid;              // 유급 여부
    private final boolean deductFromAnnual;  // 연차 차감 여부

    LeaveType(String koreanName, boolean paid, boolean deductFromAnnual) {
        this.koreanName = koreanName;
        this.paid = paid;
        this.deductFromAnnual = deductFromAnnual;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isDeductFromAnnual() {
        return deductFromAnnual;
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
