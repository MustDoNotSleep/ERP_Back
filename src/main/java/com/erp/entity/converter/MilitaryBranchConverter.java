package com.erp.entity.converter;

import com.erp.entity.enums.MilitaryBranch;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MilitaryBranchConverter implements AttributeConverter<MilitaryBranch, String> {
    
    @Override
    public String convertToDatabaseColumn(MilitaryBranch attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public MilitaryBranch convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (MilitaryBranch branch : MilitaryBranch.values()) {
            if (branch.getKoreanName().equals(dbData)) {
                return branch;
            }
        }
        
        throw new IllegalArgumentException("Unknown military branch: " + dbData);
    }
}
