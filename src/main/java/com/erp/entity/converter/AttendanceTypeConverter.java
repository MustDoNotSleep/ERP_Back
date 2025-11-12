package com.erp.entity.converter;

import com.erp.entity.enums.AttendanceType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AttendanceTypeConverter implements AttributeConverter<AttendanceType, String> {
    
    @Override
    public String convertToDatabaseColumn(AttendanceType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public AttendanceType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (AttendanceType type : AttendanceType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown attendance type: " + dbData);
    }
}
