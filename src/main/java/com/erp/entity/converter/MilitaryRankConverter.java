package com.erp.entity.converter;

import com.erp.entity.enums.MilitaryRank;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MilitaryRankConverter implements AttributeConverter<MilitaryRank, String> {
    
    @Override
    public String convertToDatabaseColumn(MilitaryRank attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getKoreanName();
    }
    
    @Override
    public MilitaryRank convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (MilitaryRank rank : MilitaryRank.values()) {
            if (rank.getKoreanName().equals(dbData)) {
                return rank;
            }
        }
        
        throw new IllegalArgumentException("Unknown military rank: " + dbData);
    }
}
