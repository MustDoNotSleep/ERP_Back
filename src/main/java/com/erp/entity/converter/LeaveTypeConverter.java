package com.erp.entity.converter;

import com.erp.entity.enums.LeaveType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LeaveTypeConverter implements AttributeConverter<LeaveType, String> {
    
    @Override
    public String convertToDatabaseColumn(LeaveType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public LeaveType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (LeaveType type : LeaveType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown leave type: " + dbData);
    }
}
