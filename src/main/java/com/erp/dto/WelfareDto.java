package com.erp.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class WelfareDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private String welfareType; // EDUCATION, BOOK, OTHER
        private YearMonth paymentMonth;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String note;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long employeeId;
        private String employeeName;
        private String departmentName;
        private String welfareType;
        private String welfareTypeName; // "교육 지원금", "도서 구입비" 등
        private YearMonth paymentMonth;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String note;
        private Long approverId;
        private String approverName;
        private Boolean isApproved;
        private LocalDateTime createdAt;
    }
    
    @Getter
    @Builder
    public static class BalanceResponse {
        private Long employeeId;
        private String employeeName;
        private YearMonth yearMonth;
        private BigDecimal grantedAmount;      // 실제 지급된 복리후생 금액
        private BigDecimal usedAmount;         // 사용한 금액
        private BigDecimal remainingAmount;    // 남은 금액
        private Double usageRate;              // 사용률 (0.0 ~ 100.0)
    }
    
    @Getter
    @Builder
    public static class ApprovalRequest {
        private Boolean approved;
        private String note;
    }
}
