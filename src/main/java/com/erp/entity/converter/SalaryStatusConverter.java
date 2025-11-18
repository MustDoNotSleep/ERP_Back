package com.erp.entity.converter;

import com.erp.entity.enums.SalaryStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SalaryStatusConverter implements AttributeConverter<SalaryStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(SalaryStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public SalaryStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (SalaryStatus status : SalaryStatus.values()) {
            if (status.getKoreanName().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown salary status: " + dbData);
    }
}
