package com.erp.entity.converter;

import com.erp.entity.enums.EmploymentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmploymentTypeConverter implements AttributeConverter<EmploymentType, String> {
    
    @Override
    public String convertToDatabaseColumn(EmploymentType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public EmploymentType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (EmploymentType type : EmploymentType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown employment type: " + dbData);
    }
}
