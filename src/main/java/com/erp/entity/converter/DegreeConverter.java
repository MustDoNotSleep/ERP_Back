package com.erp.entity.converter;

import com.erp.entity.enums.Degree;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DegreeConverter implements AttributeConverter<Degree, String> {
    
    @Override
    public String convertToDatabaseColumn(Degree attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public Degree convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (Degree degree : Degree.values()) {
            if (degree.getKoreanName().equals(dbData)) {
                return degree;
            }
        }
        
        throw new IllegalArgumentException("Unknown degree: " + dbData);
    }
}
