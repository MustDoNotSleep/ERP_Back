package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.Rewards;
import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus; // ⭐ 결재 상태 Enum
import com.erp.entity.enums.RewardType;
import com.erp.entity.enums.RewardValue;  // ⭐ 포상 사유 Enum
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardDto {

    // =======================================================
    // 1. 공통 필드
    // =======================================================
    private Long rewardId;          // PK

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate rewardDate;   // 요청일/추천일

    private RewardType rewardType;  // 포상 종류 (ENUM)
    private RewardItem rewardItem;  // 포상 형태 (ENUM)
    
    // ⭐ [수정] String -> RewardValue (Enum)으로 변경
    private RewardValue rewardValue; // 포상 사유 (TEAM_CONTRIBUTION 등)
    
    private Double amount;          // 포상 금액
    private String reason;          // 사유 (상세 내용)
    
    // ⭐ [수정] 결재 상태는 RewardStatus 사용
    private RewardStatus status;    // 상태 (PENDING, APPROVED...)

    // =======================================================
    // 2. 입력용 필드 (Request)
    // =======================================================
    private Long employeeId;        // 포상 받는 사람 사번
    private Long approverId;        // 승인자 사번

    // =======================================================
    // 3. 출력용 필드 (Response)
    // =======================================================
    private String employeeName;    // 받는 사람 이름
    private String departmentName;  // 받는 사람 부서
    private String positionName;    // 받는 사람 직급
    
    private String approverName;    // 승인자 이름

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt; // 승인 일시

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;  // 신청 일시

    // =======================================================
    // ⭐ [핵심] Entity -> DTO 변환 (조회할 때 사용)
    // =======================================================
    public static RewardDto from(Rewards entity) {
        return RewardDto.builder()
                .rewardId(entity.getRewardId())
                .rewardDate(entity.getRewardDate())
                .rewardType(entity.getRewardType())
                .rewardItem(entity.getRewardItem())
                .rewardValue(entity.getRewardValue()) // Entity도 RewardValue Enum이어야 함
                .amount(entity.getAmount())
                .reason(entity.getReason())
                
                // ⭐ [수정] getRewardStatus() -> getStatus() 로 변경 (Lombok 기본 Getter)
                .status(entity.getStatus()) 
                
                // --- 1. 받는 사람 정보 ---
                .employeeId(entity.getEmployee().getId())
                .employeeName(entity.getEmployee().getName())
                .departmentName(entity.getEmployee().getDepartment() != null 
                        ? entity.getEmployee().getDepartment().getTeamName() : "-")
                .positionName(entity.getEmployee().getPosition() != null 
                        ? entity.getEmployee().getPosition().getPositionName() : "-")

                // --- 2. 승인자 정보 ---
                .approverId(entity.getApprover() != null ? entity.getApprover().getId() : null)
                .approverName(entity.getApprover() != null ? entity.getApprover().getName() : "-") 
                
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    // =======================================================
    // Entity 변환 (저장할 때 사용)
    // =======================================================
    public Rewards toEntity(Employee targetEmployee) {
        return Rewards.builder()
                .employee(targetEmployee)
                .rewardDate(this.rewardDate)
                .rewardType(this.rewardType)
                .rewardItem(this.rewardItem)
                .rewardValue(this.rewardValue) // Enum 그대로 저장
                .amount(this.amount)
                .reason(this.reason)
                .status(RewardStatus.PENDING) // ⭐ 초기값은 Status Enum 사용
                .build();
    }
}