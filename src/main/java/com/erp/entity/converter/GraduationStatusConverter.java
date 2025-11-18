package com.erp.entity.converter;

import com.erp.entity.enums.GraduationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GraduationStatusConverter implements AttributeConverter<GraduationStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(GraduationStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public GraduationStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (GraduationStatus status : GraduationStatus.values()) {
            if (status.getKoreanName().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown graduation status: " + dbData);
    }
}
