package com.erp.config;

import com.erp.entity.enums.RewardType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RewardTypeConverter implements Converter<String, RewardType> {

    @Override
    public RewardType convert(String source) {
        if (source == null || source.trim().isEmpty() || source.equals("선택")) {
            return null;
        }
        
        // 1. 먼저 영어 상수명으로 시도 (예: "BEST_EMPLOYEE")
        try {
            return RewardType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 2. 한글 설명으로 검색 (예: "우수사원상")
            for (RewardType type : RewardType.values()) {
                if (type.getDescription().equals(source)) {
                    return type;
                }
            }
            // 3. 매칭 실패 시 null 반환
            return null;
        }
    }
}
