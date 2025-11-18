package com.erp.entity.converter;

import com.erp.entity.enums.CourseType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CourseTypeConverter implements AttributeConverter<CourseType, String> {
    
    @Override
    public String convertToDatabaseColumn(CourseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public CourseType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (CourseType type : CourseType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown course type: " + dbData);
    }
}
