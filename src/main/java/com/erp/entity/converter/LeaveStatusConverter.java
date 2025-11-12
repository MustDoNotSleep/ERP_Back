package com.erp.entity.converter;

import com.erp.entity.enums.LeaveStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LeaveStatusConverter implements AttributeConverter<LeaveStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(LeaveStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public LeaveStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (LeaveStatus status : LeaveStatus.values()) {
            if (status.getKoreanName().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown leave status: " + dbData);
    }
}
