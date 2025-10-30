package com.erp.dto;

import com.erp.entity.SalaryInfo;
import com.erp.entity.enums.BankName;
import lombok.Builder;
import lombok.Getter;

public class SalaryInfoDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private BankName bankName;
        private String accountNumber;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private BankName bankName;
        private String accountNumber;
        
        public static Response from(SalaryInfo info) {
            return Response.builder()
                .id(info.getId())
                .employeeName(info.getEmployee().getName())
                .bankName(info.getBankName())
                .accountNumber(info.getAccountNumber())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private BankName bankName;
        private String accountNumber;
    }
}
