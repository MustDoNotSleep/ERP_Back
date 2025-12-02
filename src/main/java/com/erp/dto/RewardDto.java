package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.Rewards;
import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus;
import com.erp.entity.enums.RewardType;
import com.fasterxml.jackson.annotation.JsonFormat; // ⭐ 날짜 포맷팅용 라이브러리 추가
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ⭐ 프론트로 날짜 예쁘게 보냄
    private LocalDate rewardDate;   // 요청일/추천일

    private RewardType rewardType;  // 포상 종류 (ENUM)
    private RewardItem rewardItem;  // 포상 형태 (ENUM)
    private String rewardValue;     // 포상 이유 (ENUM or String)
    private Double amount;          // 포상 금액
    private String reason;          // 사유 (상세 내용)
    private RewardStatus status;    // 상태 (PENDING, APPROVED...)

    // =======================================================
    // 2. 입력용 필드 (Request)
    // =======================================================
    private Long employeeId;        // 포상 받는 사람 사번
    private Long approverId;        // 승인자 사번

    // =======================================================
    // 3. 출력용 필드 (Response - 화면에 보여줄 이름들)
    // =======================================================
    private String employeeName;    // 받는 사람 이름
    private String departmentName;  // 받는 사람 부서
    private String positionName;    // 받는 사람 직급
    
    private String approverName;    // ⭐ 승인자 이름 (화면에 표시될 핵심!)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // ⭐ 시간까지 예쁘게
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
                .rewardValue(entity.getRewardValue())
                .amount(entity.getAmount())
                .reason(entity.getReason())
                .status(entity.getStatus())
                
                // --- 1. 받는 사람 정보 (Mandatory) ---
                .employeeId(entity.getEmployee().getId()) // 사번
                .employeeName(entity.getEmployee().getName()) // 이름
                // 부서/직급이 없을 경우를 대비한 안전 장치
                .departmentName(entity.getEmployee().getDepartment() != null 
                        ? entity.getEmployee().getDepartment().getTeamName() : "-")
                .positionName(entity.getEmployee().getPosition() != null 
                        ? entity.getEmployee().getPosition().getPositionName() : "-")

                // --- 2. 승인자 정보 (Optional - 여기가 제일 중요!) ---
                // 승인자(Approver)가 아직 없는 상태(대기중)일 때 에러 안 나게 처리
                .approverId(entity.getApprover() != null ? entity.getApprover().getId() : null)
                
                // ⭐ 승인자가 있으면 이름 넣고, 없으면 "-" 또는 "미승인" 처리
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
                .rewardValue(this.rewardValue)
                .amount(this.amount)
                .reason(this.reason)
                .status(RewardStatus.PENDING) // 처음 생성 시엔 무조건 대기
                .build();
    }
}