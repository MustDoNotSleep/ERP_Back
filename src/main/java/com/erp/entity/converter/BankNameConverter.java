package com.erp.entity.converter;

import com.erp.entity.enums.BankName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BankNameConverter implements AttributeConverter<BankName, String> {
    
    @Override
    public String convertToDatabaseColumn(BankName attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public BankName convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (BankName bank : BankName.values()) {
            if (bank.getKoreanName().equals(dbData)) {
                return bank;
            }
        }
        
        throw new IllegalArgumentException("Unknown bank name: " + dbData);
    }
}
