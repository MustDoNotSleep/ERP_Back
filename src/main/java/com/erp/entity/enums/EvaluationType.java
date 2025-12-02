package com.erp.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum EvaluationType {
    KPI("KPI 평가"),
    LEADERSHIP("리더십 평가");

    private final String description;


    EvaluationType(String description) {
        this.description = description;
    }

}
