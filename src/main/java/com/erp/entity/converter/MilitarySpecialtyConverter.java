package com.erp.entity.converter;

import com.erp.entity.enums.MilitarySpecialty;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MilitarySpecialtyConverter implements AttributeConverter<MilitarySpecialty, String> {
    
    @Override
    public String convertToDatabaseColumn(MilitarySpecialty attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public MilitarySpecialty convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (MilitarySpecialty specialty : MilitarySpecialty.values()) {
            if (specialty.getKoreanName().equals(dbData)) {
                return specialty;
            }
        }
        
        throw new IllegalArgumentException("Unknown military specialty: " + dbData);
    }
}
