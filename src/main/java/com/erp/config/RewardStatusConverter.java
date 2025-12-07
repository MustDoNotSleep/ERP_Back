package com.erp.config;

import com.erp.entity.enums.RewardStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RewardStatusConverter implements Converter<String, RewardStatus> {

    @Override
    public RewardStatus convert(String source) {
        if (source == null || source.trim().isEmpty() || source.equals("선택")) {
            return null;
        }
        
        // 1. 먼저 영어 상수명으로 시도 (예: "APPROVED")
        try {
            return RewardStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 2. 한글 설명으로 검색 (예: "승인")
            for (RewardStatus status : RewardStatus.values()) {
                if (status.getDescription().equals(source)) {
                    return status;
                }
            }
            // 3. 매칭 실패 시 null 반환
            return null;
        }
    }
}
