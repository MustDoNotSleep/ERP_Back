package com.erp.entity.enums;

// 군별 (militaryBranch)
public enum MilitaryBranch {
    YUK_GOON("육군"), HAE_GOON("해군"), GONG_GOON("공군"), HAEDAE("해병대"), ETC("기타");

    private final String koreanName;
    MilitaryBranch(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
