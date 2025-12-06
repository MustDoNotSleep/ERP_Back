package com.erp.controller;

import com.erp.dto.RewardDto;
import com.erp.entity.enums.RewardStatus; // ⭐ [수정] 결재 상태 Enum 임포트
import com.erp.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hr/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    // =================================================================================
    // 🔍 [GET] 포상 목록 조회
    // =================================================================================
    @GetMapping
    public ResponseEntity<List<RewardDto>> getRewards(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) String positionName,
            
            // 🚨 [수정 중요] RewardValue -> RewardStatus 로 변경하고, 변수명도 status로 통일!
            @RequestParam(required = false) RewardStatus status
    ) {
        // 이제 파라미터 이름(status)과 서비스 파라미터가 일치합니다.
        List<RewardDto> result = rewardService.searchRewards(startDate, endDate, empName, positionName, deptName, status);
        return ResponseEntity.ok(result);
    }

    // =================================================================================
    // 💾 [POST] 포상 등록
    // =================================================================================
    @PostMapping
    public ResponseEntity<String> createReward(@RequestBody RewardDto dto) {
        try {
            rewardService.createReward(dto);
            return ResponseEntity.ok("포상 등록이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // =================================================================================
    // ✅ [PUT] 포상 승인 처리
    // =================================================================================
    @PutMapping("/{rewardId}/approve")
    public ResponseEntity<String> approveReward(@PathVariable Long rewardId) {
        try {
            rewardService.approveReward(rewardId);
            return ResponseEntity.ok("승인 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =================================================================================
    // ❌ [PUT] 포상 반려 처리
    // =================================================================================
    @PutMapping("/{rewardId}/reject")
    public ResponseEntity<String> rejectReward(@PathVariable Long rewardId) {
        try {
            rewardService.rejectReward(rewardId);
            return ResponseEntity.ok("반려 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =================================================================================
    // 🗑️ [DELETE] 포상 내역 삭제
    // =================================================================================
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<String> deleteReward(@PathVariable Long rewardId) {
        try {
            rewardService.deleteReward(rewardId);
            return ResponseEntity.ok("삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}