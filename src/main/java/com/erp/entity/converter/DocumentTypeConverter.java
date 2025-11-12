package com.erp.entity.converter;

import com.erp.entity.enums.DocumentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {
    
    @Override
    public String convertToDatabaseColumn(DocumentType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (DocumentType type : DocumentType.values()) {
            if (type.getKoreanName().equals(dbData)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown document type: " + dbData);
    }
}
