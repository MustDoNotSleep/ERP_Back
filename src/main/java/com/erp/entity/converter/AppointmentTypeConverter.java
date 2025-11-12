package com.erp.entity.converter;

import com.erp.entity.enums.AppointmentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppointmentTypeConverter implements AttributeConverter<AppointmentType, String> {
    
    @Override
    public String convertToDatabaseColumn(AppointmentType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public AppointmentType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (AppointmentType type : AppointmentType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown appointment type: " + dbData);
    }
}
