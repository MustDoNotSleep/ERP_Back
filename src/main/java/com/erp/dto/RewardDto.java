package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.Rewards;
import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus;
import com.erp.entity.enums.RewardType;
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
    // 1. 공통 필드 (입력/출력 모두 사용)
    // =======================================================
    private Long rewardId;          // PK (입력 땐 null, 출력 땐 값 있음)
    private LocalDate rewardDate;   // 요청일/추천일
    private RewardType rewardType;  // 포상 종류 (ENUM)
    private RewardItem rewardItem;  // 포상 형태 (ENUM)
    private String rewardValue;     // 포상 가치 (금액 등)
    private String reason;          // 사유
    private RewardStatus status;    // 상태 (대기/승인/반려)

    // =======================================================
    // 2. 입력용 필드 (Request: 프론트 -> 백엔드 보낼 때)
    // =======================================================
    private Long employeeId;        // 포상 받는 사람 사번 (저장할 때 필수)
    private Long approverId;        // 승인자 사번 (승인할 때 필요)

    // =======================================================
    // 3. 출력용 필드 (Response: 백엔드 -> 프론트 보여줄 때)
    // =======================================================
    private String employeeName;    // 받는 사람 이름
    private String departmentName;  // 받는 사람 부서
    private String positionName;    // 받는 사람 직급
    
    private String approverName;    // 승인자 이름
    private LocalDateTime approvedAt; // 승인 일시
    private LocalDateTime createdAt;  // 신청 일시

    // =======================================================
    // ⭐ 편의 메서드: Entity -> DTO 변환 (조회할 때 사용)
    // =======================================================
    public static RewardDto from(Rewards entity) {
        return RewardDto.builder()
                .rewardId(entity.getRewardId())
                .rewardDate(entity.getRewardDate())
                .rewardType(entity.getRewardType())
                .rewardItem(entity.getRewardItem())
                .rewardValue(entity.getRewardValue())
                .reason(entity.getReason())
                .status(entity.getStatus())
                
                // 받는 사람 정보
                .employeeId(entity.getEmployee().getId())
                .employeeName(entity.getEmployee().getName())
                .departmentName(entity.getEmployee().getDepartment() != null 
                        ? entity.getEmployee().getDepartment().getTeamName() : "-")
                .positionName(entity.getEmployee().getPosition() != null 
                        ? entity.getEmployee().getPosition().getPositionName() : "-")

                // 승인자 정보 (없을 수도 있으니 null 체크)
                .approverId(entity.getApprover() != null ? entity.getApprover().getId() : null)
                .approverName(entity.getApprover() != null ? entity.getApprover().getName() : "-")
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    // =======================================================
    // ⭐ 편의 메서드: DTO -> Entity 변환 (저장할 때 사용)
    // =======================================================
    public Rewards toEntity(Employee targetEmployee) {
        return Rewards.builder()
                .employee(targetEmployee)
                .rewardDate(this.rewardDate)
                .rewardType(this.rewardType)
                .rewardItem(this.rewardItem)
                .rewardValue(this.rewardValue)
                .reason(this.reason)
                .status(RewardStatus.PENDING) // 처음엔 무조건 대기
                .build();
    }
}