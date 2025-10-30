package com.erp.dto;

import com.erp.entity.MilitaryServiceInfo;
import com.erp.entity.enums.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class MilitaryServiceInfoDto {
    
    @Getter
    @Builder
    public static class Request {
        private Long employeeId;
        private MilitaryStatus militaryStatus;
        private MilitaryBranch militaryBranch;
        private MilitaryRank militaryRank;
        private MilitarySpecialty militarySpecialty;
        private ExemptionReason exemptionReason;
        private LocalDate serviceStartDate;
        private LocalDate serviceEndDate;
    }
    
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String employeeName;
        private MilitaryStatus militaryStatus;
        private MilitaryBranch militaryBranch;
        private MilitaryRank militaryRank;
        private MilitarySpecialty militarySpecialty;
        private ExemptionReason exemptionReason;
        private LocalDate serviceStartDate;
        private LocalDate serviceEndDate;
        
        public static Response from(MilitaryServiceInfo info) {
            return Response.builder()
                .id(info.getId())
                .employeeName(info.getEmployee().getName())
                .militaryStatus(info.getMilitaryStatus())
                .militaryBranch(info.getMilitaryBranch())
                .militaryRank(info.getMilitaryRank())
                .militarySpecialty(info.getMilitarySpecialty())
                .exemptionReason(info.getExemptionReason())
                .serviceStartDate(info.getServiceStartDate())
                .serviceEndDate(info.getServiceEndDate())
                .build();
        }
    }
    
    @Getter
    @Builder
    public static class UpdateRequest {
        private MilitaryStatus militaryStatus;
        private MilitaryBranch militaryBranch;
        private MilitaryRank militaryRank;
        private MilitarySpecialty militarySpecialty;
        private ExemptionReason exemptionReason;
        private LocalDate serviceStartDate;
        private LocalDate serviceEndDate;
    }
}
