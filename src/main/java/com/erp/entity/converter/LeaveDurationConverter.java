package com.erp.entity.converter;

import com.erp.entity.enums.LeaveDuration;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LeaveDurationConverter implements AttributeConverter<LeaveDuration, String> {
    
    @Override
    public String convertToDatabaseColumn(LeaveDuration attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public LeaveDuration convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (LeaveDuration duration : LeaveDuration.values()) {
            if (duration.getKoreanName().equals(dbData)) {
                return duration;
            }
        }
        
        throw new IllegalArgumentException("Unknown leave duration: " + dbData);
    }
}
