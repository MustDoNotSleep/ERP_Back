package com.erp.entity.enums;

// 면제 사유 (exemptionReason)
public enum ExemptionReason {
    BOG_MU_DAE_GI("복무대기"), SANG_YE_GYEONG("생계곤란"), JIL_BYEONG("질병"), ETC("기타");

    private final String koreanName;
    ExemptionReason(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
