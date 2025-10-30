package com.erp.entity.converter;

import com.erp.entity.enums.Nationality;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NationalityConverter implements AttributeConverter<Nationality, String> {
    
    @Override
    public String convertToDatabaseColumn(Nationality attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public Nationality convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (Nationality nationality : Nationality.values()) {
            if (nationality.getKoreanName().equals(dbData)) {
                return nationality;
            }
        }
        
        throw new IllegalArgumentException("Unknown nationality: " + dbData);
    }
}
