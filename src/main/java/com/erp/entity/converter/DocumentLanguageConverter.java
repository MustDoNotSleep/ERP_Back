package com.erp.entity.converter;

import com.erp.entity.enums.DocumentLanguage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentLanguageConverter implements AttributeConverter<DocumentLanguage, String> {
    
    @Override
    public String convertToDatabaseColumn(DocumentLanguage attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public DocumentLanguage convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (DocumentLanguage language : DocumentLanguage.values()) {
            if (language.getKoreanName().equals(dbData)) {
                return language;
            }
        }
        
        throw new IllegalArgumentException("Unknown document language: " + dbData);
    }
}
