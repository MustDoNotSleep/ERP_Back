package com.erp.entity.converter;

import com.erp.entity.enums.RewardValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RewardValueConverter implements AttributeConverter<RewardValue, String> {

    @Override
    public String convertToDatabaseColumn(RewardValue attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public RewardValue convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return RewardValue.valueOf(dbData);
        } catch (IllegalArgumentException ex) {
            RewardValue resolved = RewardValue.from(dbData);
            return resolved != null ? resolved : RewardValue.ETC;
        }
    }
}
