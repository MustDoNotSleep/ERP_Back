package com.erp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 급여 관련 세금 및 4대보험 자동 계산 유틸리티
 * 2024년 기준 간이세액표 및 법정 요율 적용
 */
public class TaxCalculator {
    
    // 4대보험 요율 (2024년 기준)
    private static final BigDecimal NATIONAL_PENSION_RATE = new BigDecimal("0.045");      // 국민연금 4.5%
    private static final BigDecimal HEALTH_INSURANCE_RATE = new BigDecimal("0.03545");    // 건강보험 3.545%
    // private static final BigDecimal LONG_TERM_CARE_RATE = new BigDecimal("0.004591");     // 장기요양보험 0.4591% (건강보험의 12.95%)
    private static final BigDecimal EMPLOYMENT_INSURANCE_RATE = new BigDecimal("0.009");  // 고용보험 0.9%
    
    // 국민연금 상한액 및 하한액 (2024년 기준)
    private static final BigDecimal PENSION_MIN_BASE = new BigDecimal("370000");  // 최저 기준소득월액
    private static final BigDecimal PENSION_MAX_BASE = new BigDecimal("5900000"); // 최고 기준소득월액
    
    // 건강보험 상한액 (2024년 기준)
    private static final BigDecimal HEALTH_MAX_BASE = new BigDecimal("8653000"); // 최고 기준소득월액
    
    /**
     * 소득세 계산 (간이세액표 적용)
     * 2024년 근로소득 간이세액표 기준 (월 급여, 부양가족 1명 기준)
     * 
     * @param monthlyIncome 월 총 급여 (과세 대상 금액)
     * @return 소득세
     */
    public static BigDecimal calculateIncomeTax(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal tax = BigDecimal.ZERO;
        
        // 2024년 간이세액표 (부양가족 1명 기준, 80% 세율)
        if (monthlyIncome.compareTo(new BigDecimal("1060000")) < 0) {
            // 106만원 미만: 비과세
            tax = BigDecimal.ZERO;
        } else if (monthlyIncome.compareTo(new BigDecimal("1500000")) < 0) {
            // 106만원 ~ 150만원
            tax = monthlyIncome.subtract(new BigDecimal("1060000"))
                    .multiply(new BigDecimal("0.04"));
        } else if (monthlyIncome.compareTo(new BigDecimal("2000000")) < 0) {
            // 150만원 ~ 200만원
            tax = new BigDecimal("17600")
                    .add(monthlyIncome.subtract(new BigDecimal("1500000"))
                    .multiply(new BigDecimal("0.05")));
        } else if (monthlyIncome.compareTo(new BigDecimal("2500000")) < 0) {
            // 200만원 ~ 250만원
            tax = new BigDecimal("42600")
                    .add(monthlyIncome.subtract(new BigDecimal("2000000"))
                    .multiply(new BigDecimal("0.07")));
        } else if (monthlyIncome.compareTo(new BigDecimal("3000000")) < 0) {
            // 250만원 ~ 300만원
            tax = new BigDecimal("77600")
                    .add(monthlyIncome.subtract(new BigDecimal("2500000"))
                    .multiply(new BigDecimal("0.08")));
        } else if (monthlyIncome.compareTo(new BigDecimal("4500000")) < 0) {
            // 300만원 ~ 450만원
            tax = new BigDecimal("117600")
                    .add(monthlyIncome.subtract(new BigDecimal("3000000"))
                    .multiply(new BigDecimal("0.12")));
        } else if (monthlyIncome.compareTo(new BigDecimal("6000000")) < 0) {
            // 450만원 ~ 600만원
            tax = new BigDecimal("297600")
                    .add(monthlyIncome.subtract(new BigDecimal("4500000"))
                    .multiply(new BigDecimal("0.15")));
        } else if (monthlyIncome.compareTo(new BigDecimal("8800000")) < 0) {
            // 600만원 ~ 880만원
            tax = new BigDecimal("522600")
                    .add(monthlyIncome.subtract(new BigDecimal("6000000"))
                    .multiply(new BigDecimal("0.24")));
        } else if (monthlyIncome.compareTo(new BigDecimal("15000000")) < 0) {
            // 880만원 ~ 1500만원
            tax = new BigDecimal("1194600")
                    .add(monthlyIncome.subtract(new BigDecimal("8800000"))
                    .multiply(new BigDecimal("0.35")));
        } else {
            // 1500만원 이상
            tax = new BigDecimal("3361600")
                    .add(monthlyIncome.subtract(new BigDecimal("15000000"))
                    .multiply(new BigDecimal("0.38")));
        }
        
        // 10원 단위 절사
        return tax.setScale(0, RoundingMode.DOWN)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));
    }
    
    /**
     * 지방소득세 계산 (소득세의 10%)
     * 
     * @param incomeTax 소득세
     * @return 지방소득세
     */
    public static BigDecimal calculateLocalIncomeTax(BigDecimal incomeTax) {
        if (incomeTax == null || incomeTax.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 10원 단위 절사
        return incomeTax.multiply(new BigDecimal("0.1"))
                .setScale(0, RoundingMode.DOWN)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));
    }
    
    /**
     * 국민연금 계산 (4.5%)
     * 기준소득월액 상하한 적용
     * 
     * @param monthlyIncome 월 총 급여
     * @return 국민연금
     */
    public static BigDecimal calculateNationalPension(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 기준소득월액 상하한 적용
        BigDecimal baseIncome = monthlyIncome;
        if (baseIncome.compareTo(PENSION_MIN_BASE) < 0) {
            baseIncome = PENSION_MIN_BASE;
        } else if (baseIncome.compareTo(PENSION_MAX_BASE) > 0) {
            baseIncome = PENSION_MAX_BASE;
        }
        
        // 10원 단위 절사
        return baseIncome.multiply(NATIONAL_PENSION_RATE)
                .setScale(0, RoundingMode.DOWN)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));
    }
    
    /**
     * 건강보험료 계산 (3.545% + 장기요양보험 0.4591%)
     * 
     * @param monthlyIncome 월 총 급여
     * @return 건강보험료 (장기요양보험료 포함)
     */
    public static BigDecimal calculateHealthInsurance(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 기준소득월액 상한 적용
        BigDecimal baseIncome = monthlyIncome;
        if (baseIncome.compareTo(HEALTH_MAX_BASE) > 0) {
            baseIncome = HEALTH_MAX_BASE;
        }
        
        // 건강보험료
        BigDecimal healthInsurance = baseIncome.multiply(HEALTH_INSURANCE_RATE)
                .setScale(0, RoundingMode.DOWN);
        
        // 장기요양보험료 (건강보험료의 12.95%)
        BigDecimal longTermCare = healthInsurance.multiply(new BigDecimal("0.1295"))
                .setScale(0, RoundingMode.DOWN);
        
        // 10원 단위 절사
        return healthInsurance.add(longTermCare)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));
    }
    
    /**
     * 고용보험료 계산 (0.9%)
     * 
     * @param monthlyIncome 월 총 급여
     * @return 고용보험료
     */
    public static BigDecimal calculateEmploymentInsurance(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 10원 단위 절사
        return monthlyIncome.multiply(EMPLOYMENT_INSURANCE_RATE)
                .setScale(0, RoundingMode.DOWN)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));
    }
    
    /**
     * 전체 공제액 계산 (세금 + 4대보험)
     * 
     * @param monthlyIncome 월 총 급여
     * @return 총 공제액
     */
    public static BigDecimal calculateTotalDeductions(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal incomeTax = calculateIncomeTax(monthlyIncome);
        BigDecimal localIncomeTax = calculateLocalIncomeTax(incomeTax);
        BigDecimal nationalPension = calculateNationalPension(monthlyIncome);
        BigDecimal healthInsurance = calculateHealthInsurance(monthlyIncome);
        BigDecimal employmentInsurance = calculateEmploymentInsurance(monthlyIncome);
        
        return incomeTax
                .add(localIncomeTax)
                .add(nationalPension)
                .add(healthInsurance)
                .add(employmentInsurance);
    }
    
    /**
     * 실수령액 계산
     * 
     * @param monthlyIncome 월 총 급여
     * @return 실수령액
     */
    public static BigDecimal calculateNetSalary(BigDecimal monthlyIncome) {
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalDeductions = calculateTotalDeductions(monthlyIncome);
        return monthlyIncome.subtract(totalDeductions);
    }
}
