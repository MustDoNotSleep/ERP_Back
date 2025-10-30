package com.erp.entity.enums;

// 3. BankName (은행 이름)
public enum BankName {
    HANA_BANK("하나은행"), SHINHAN_BANK("신한은행"), WOORI_BANK("우리은행"), 
    KOOKMIN_BANK("국민은행"), NONGHYUP_BANK("농협은행"), GIB_BANK("기업은행"), 
    KAKAO_BANK("카카오뱅크"), TOSS_BANK("토스뱅크"), ETC("기타");

    private final String koreanName;
    BankName(String koreanName) { this.koreanName = koreanName; }
    public String getKoreanName() { return koreanName; }
}