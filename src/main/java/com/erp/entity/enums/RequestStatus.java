package com.erp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 9. Status (요청 상태 - AppointmentRequest, Course 공통 사용)
public enum RequestStatus {
    PENDING("대기"), 
    APPROVED("최종승인"), 
    REJECTED("반려");

    private final String koreanName;
    
    RequestStatus(String koreanName) { 
        this.koreanName = koreanName; 
    }
    
    @JsonValue
    public String getKoreanName() { 
        return koreanName; 
    }
    
    @JsonCreator
    public static RequestStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PENDING; // 기본값
        }
        
        // 영어 constant 이름으로 매칭
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        
        // 한글 이름으로 매칭
        for (RequestStatus status : RequestStatus.values()) {
            if (status.koreanName.equals(value)) {
                return status;
            }
        }
        
        // "승인" -> APPROVED 매핑
        if ("승인".equals(value)) {
            return APPROVED;
        }
        
        throw new IllegalArgumentException("Invalid RequestStatus: " + value + 
            ". Allowed values: PENDING(대기), APPROVED(최종승인), REJECTED(반려)");
    }
}
