package com.erp.service;

import com.erp.dto.RewardDto;
import com.erp.entity.Employee;
import com.erp.entity.Rewards;
import com.erp.entity.enums.RewardStatus;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.RewardRepository;
import com.erp.util.SecurityUtil; // ⭐ 로그인한 사용자 정보 가져오기용
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final EmployeeRepository employeeRepository;

    // =================================================================================
    // 💾 [POST] 포상 등록
    // =================================================================================
    @Transactional
    public void createReward(RewardDto dto) {
        // 1. 포상 받는 사원이 실제로 존재하는지 확인
        Employee targetEmployee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다. ID=" + dto.getEmployeeId()));

        // 2. DTO -> Entity 변환 (기본 상태 PENDING)
        Rewards reward = dto.toEntity(targetEmployee);

        // 3. 저장
        rewardRepository.save(reward);
    }

    // =================================================================================
    // 🔍 [GET] 포상 목록 조회 (검색 필터 적용)
    // =================================================================================
    @Transactional(readOnly = true)
    public List<RewardDto> searchRewards(
            LocalDate startDate,
            LocalDate endDate,
            String empName,
            String deptName,
            RewardStatus status
    ) {
        // Repository의 동적 쿼리 호출 -> DTO 변환 후 반환
        return rewardRepository.searchRewards(startDate, endDate, empName, deptName, status).stream()
                .map(RewardDto::from)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // ✅ [PUT] 포상 승인 처리
    // =================================================================================
    @Transactional
    public void approveReward(Long rewardId) {
        // 1. 포상 내역 찾기
        Rewards reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포상 내역입니다. ID=" + rewardId));

        // 2. 현재 로그인한 사람(승인자) 찾기
        Long currentAdminId = SecurityUtil.getCurrentEmployeeId();
        Employee approver = employeeRepository.findById(currentAdminId)
                .orElseThrow(() -> new IllegalArgumentException("승인자(로그인 사용자) 정보를 찾을 수 없습니다."));

        // 3. 승인 처리 (상태 변경 + 승인자 기록)
        reward.approve(approver);
    }

    // =================================================================================
    // ❌ [PUT] 포상 반려 처리
    // =================================================================================
    @Transactional
    public void rejectReward(Long rewardId) {
        // 1. 포상 내역 찾기
        Rewards reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포상 내역입니다. ID=" + rewardId));

        // 2. 현재 로그인한 사람(반려자) 찾기
        Long currentAdminId = SecurityUtil.getCurrentEmployeeId();
        Employee rejector = employeeRepository.findById(currentAdminId)
                .orElseThrow(() -> new IllegalArgumentException("반려자(로그인 사용자) 정보를 찾을 수 없습니다."));

        // 3. 반려 처리
        reward.reject(rejector);
    }

    // =================================================================================
    // 🗑️ [DELETE] 포상 삭제 (필요 시)
    // =================================================================================
    @Transactional
    public void deleteReward(Long rewardId) {
        if (!rewardRepository.existsById(rewardId)) {
            throw new IllegalArgumentException("삭제할 포상 내역이 존재하지 않습니다.");
        }
        rewardRepository.deleteById(rewardId);
    }
}