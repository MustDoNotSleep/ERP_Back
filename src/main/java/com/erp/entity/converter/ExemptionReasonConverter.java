package com.erp.entity.converter;

import com.erp.entity.enums.ExemptionReason;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ExemptionReasonConverter implements AttributeConverter<ExemptionReason, String> {
    
    @Override
    public String convertToDatabaseColumn(ExemptionReason attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public ExemptionReason convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (ExemptionReason reason : ExemptionReason.values()) {
            if (reason.getKoreanName().equals(dbData)) {
                return reason;
            }
        }
        
        throw new IllegalArgumentException("Unknown exemption reason: " + dbData);
    }
}
