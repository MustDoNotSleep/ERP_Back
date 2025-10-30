package com.erp.entity.enums;

// 병역 상태 (militaryStatus)
public enum MilitaryStatus {
    HYEON_YEOK("현역"), MIBOK("미복"), HYEON_YEOK_EOP_SEUM("현역;면제받았음"), ETC("기타");

    private final String koreanName;
    MilitaryStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 군별 (militaryBranch)
public enum MilitaryBranch {
    YUK_GOON("육군"), HAE_GOON("해군"), GONG_GOON("공군"), HAEDAE("해병대"), ETC("기타");

    private final String koreanName;
    MilitaryBranch(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 계급 (militaryRank)
public enum MilitaryRank {
    BYEONG_JANG("병장"), SANG_BYEONG("상병"), IL_BYEONG("일병"), HA_SA("하사"), ETC("기타");
    // 실제 계급이 더 많지만, 예시로 4개만 정의했습니다.

    private final String koreanName;
    MilitaryRank(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 특기/주특기 (militarySpecialty)
public enum MilitarySpecialty {
    BOB_YUNG("보병"), PO_BYEONG("포병"), TONG_SIN("통신"), GONG_BYEONG("공병"), ETC("기타");

    private final String koreanName;
    MilitarySpecialty(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// 면제 사유 (exemptionReason)
public enum ExemptionReason {
    BOG_MU_DAE_GI("복무대기"), SANG_YE_GYEONG("생계곤란"), JIL_BYEONG("질병"), ETC("기타");

    private final String koreanName;
    ExemptionReason(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}