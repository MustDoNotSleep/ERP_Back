package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveType {
    // 연차 관련
    ANNUAL("연차", true, true, 0, 0, "근로기준법에 따라 발생한 연차 사용"),
    
    // 병가 관련
    SICK("병가", false, false, 0, 0, "무급 병가"),
    SICK_PAID("유급병가", true, false, 1, 3, "회사 복리후생, 연간 최대 3일"),
    
    // 출산/육아 관련
    MATERNITY("출산휴가", true, false, 90, 90, "근로기준법 제74조, 출산 전후 90일 (다태아 120일)"),
    PATERNITY("배우자출산휴가", true, false, 10, 10, "남녀고용평등법 제18조의2, 배우자 출산 시 10일"),
    CHILDCARE("육아휴직", false, false, 30, 365, "육아휴직법, 최대 1년"),
    
    // 경조사 관련
    MARRIAGE("결혼휴가", true, false, 5, 5, "본인 결혼 시 5일"),
    FAMILY_MARRIAGE("가족결혼휴가", true, false, 1, 1, "자녀 및 형제자매 결혼 시 1일"),
    BEREAVEMENT("경조사", true, false, 1, 5, "부모 5일, 조부모/배우자부모 3일, 형제자매 1일"),
    
    // 기타
    OFFICIAL("공가", true, false, 1, 30, "병역, 공무 수행, 선거 등"),
    UNPAID("무급휴가", false, false, 0, 0, "개인 사유에 따른 무급 휴가");

    private final String koreanName;
    private final boolean paid;              // 유급 여부
    private final boolean deductFromAnnual;  // 연차 차감 여부
    private final int minDays;              // 최소 일수
    private final int maxDays;              // 최대 일수
    private final String description;        // 설명

    LeaveType(String koreanName, boolean paid, boolean deductFromAnnual, 
              int minDays, int maxDays, String description) {
        this.koreanName = koreanName;
        this.paid = paid;
        this.deductFromAnnual = deductFromAnnual;
        this.minDays = minDays;
        this.maxDays = maxDays;
        this.description = description;
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

    public int getMinDays() {
        return minDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 휴가 일수 유효성 검증
     * @param days 신청하려는 휴가 일수
     * @return 유효 여부
     */
    public boolean isValidDays(double days) {
        // ANNUAL, SICK, UNPAID는 일수 제한 없음 (maxDays = 0)
        if (maxDays == 0) {
            return true;
        }
        
        // 최소/최대 일수 범위 체크
        return days >= minDays && days <= maxDays;
    }

    /**
     * 휴가 일수 범위 설명 반환
     * @return "정확히 90일", "1-5일", "제한없음" 등
     */
    public String getDaysRangeDescription() {
        if (maxDays == 0) {
            return "제한없음";
        }
        if (minDays == maxDays) {
            return "정확히 " + maxDays + "일";
        }
        return minDays + "-" + maxDays + "일";
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
