package com.erp.entity.enums;

// degree (학위/졸업 구분)
public enum Degree {
    HIGH_SCHOOL("고등학교 졸업"), JUNIOR_COLLEGE("전문학사"), BACHELOR("학사"), 
    MASTER("석사"), DOCTOR("박사"), ETC("기타");

    private final String koreanName;
    Degree(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}

// graduationStatus (졸업 상태)
public enum GraduationStatus {
    GRADUATED("졸업"), COMPLETED("수료"), ATTENDING("재학"), DROPPED("중퇴");

    private final String koreanName;
    GraduationStatus(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}