package com.erp.entity.converter;

import com.erp.entity.enums.ApplicationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApplicationStatusConverter implements AttributeConverter<ApplicationStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(ApplicationStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public ApplicationStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.getKoreanName().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown application status: " + dbData);
    }
}
