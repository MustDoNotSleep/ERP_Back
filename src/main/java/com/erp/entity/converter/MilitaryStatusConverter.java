package com.erp.entity.converter;

import com.erp.entity.enums.MilitaryStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MilitaryStatusConverter implements AttributeConverter<MilitaryStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(MilitaryStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public MilitaryStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (MilitaryStatus status : MilitaryStatus.values()) {
            if (status.getKoreanName().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown military status: " + dbData);
    }
}
