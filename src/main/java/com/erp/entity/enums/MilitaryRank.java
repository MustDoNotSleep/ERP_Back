package com.erp.entity.enums;

// 계급 (militaryRank)
public enum MilitaryRank {
    BYEONG_JANG("병장"), SANG_BYEONG("상병"), IL_BYEONG("일병"), HA_SA("하사"), ETC("기타");

    private final String koreanName;
    MilitaryRank(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}
