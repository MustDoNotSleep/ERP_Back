package com.erp.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeveranceDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculationRequest {
        private Long employeeId;
        private LocalDate severanceDate; // 퇴사일 (null이면 오늘 날짜 기준 계산)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculationResponse {
        private Long employeeId;
        private String employeeName;
        private String departmentName;
        private String positionName;
        
        // 재직 정보
        private LocalDate hireDate;
        private LocalDate severanceDate;
        private Long totalWorkDays; // 총 재직일수
        private Long workYears; // 재직년수
        
        // 급여 정보
        private BigDecimal averageDailyWage; // 1일 평균임금
        private BigDecimal last3MonthsAverage; // 최근 3개월 평균 급여
        private BigDecimal monthlyBaseSalary; // 월 기본급 (참고)
        
        // 퇴직금 계산
        private BigDecimal severancePay; // 최종 퇴직금
        private BigDecimal severancePayBeforeTax; // 세전 퇴직금
        private BigDecimal estimatedTax; // 예상 세금 (참고용)
        
        // 계산식 설명
        private String calculationFormula;
        private String note;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkCalculationRequest {
        private LocalDate severanceDate; // 기준일 (null이면 오늘)
    }
}
