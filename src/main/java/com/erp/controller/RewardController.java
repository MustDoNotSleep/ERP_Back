package com.erp.controller;

import com.erp.dto.RewardDto;
import com.erp.entity.enums.RewardItem;
import com.erp.entity.enums.RewardStatus; // ⭐ [수정] 결재 상태 Enum 임포트
import com.erp.entity.enums.RewardType;
import com.erp.entity.enums.RewardValue;
import com.erp.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

import java.time.LocalDate;

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
            @RequestParam(required = false) String employeeName,  // ⭐ 프론트와 일치시키기 위해 employeeName 사용
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) String positionName,
            @RequestParam(required = false) RewardType rewardType,  // ⭐ 포상 종류 필터 추가
            @RequestParam(required = false) RewardStatus status     // 결재 상태 필터
    ) {
        System.out.println("==========================================");
        System.out.println(">>> 포상 조회 요청 도착!");
        System.out.println(">>> startDate: " + startDate);
        System.out.println(">>> endDate: " + endDate);
        System.out.println(">>> employeeName: " + employeeName);
        System.out.println(">>> deptName: " + deptName);
        System.out.println(">>> positionName: " + positionName);
        System.out.println(">>> rewardType: " + rewardType);
        System.out.println(">>> status: " + status);
        System.out.println("==========================================");
                
        List<RewardDto> result = rewardService.searchRewards(
            startDate, endDate, employeeName, deptName, positionName, rewardType, status
        );
        
        System.out.println(">>> 조회 결과 개수: " + result.size());
        System.out.println("==========================================");
        
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

    // 이넘 속성 호출용
    @GetMapping("/enums")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getRewardEnums() {
    Map<String, List<Map<String, String>>> enums = new HashMap<>();
    
    // RewardType Enum
    List<Map<String, String>> rewardTypes = Arrays.stream(RewardType.values())
        .map(type -> {
            Map<String, String> map = new HashMap<>();
            map.put("value", type.name());
            map.put("label", type.getDescription());  // ⭐ getDisplayName() -> getDescription()
            return map;
        })
        .collect(Collectors.toList());
    enums.put("rewardTypes", rewardTypes);
    
    // RewardItem Enum
    List<Map<String, String>> rewardItems = Arrays.stream(RewardItem.values())
        .map(item -> {
            Map<String, String> map = new HashMap<>();
            map.put("value", item.name());
            map.put("label", item.getDescription());  // ⭐ getDisplayName() -> getDescription()
            return map;
        })
        .collect(Collectors.toList());
    enums.put("rewardItems", rewardItems);
    
    // RewardValue Enum
    List<Map<String, String>> rewardValues = Arrays.stream(RewardValue.values())
        .map(value -> {
            Map<String, String> map = new HashMap<>();
            map.put("value", value.name());
            map.put("label", value.getDescription());  // ⭐ getDisplayName() -> getDescription()
            return map;
        })
        .collect(Collectors.toList());
    enums.put("rewardValues", rewardValues);
    
    return ResponseEntity.ok(enums);
}
}