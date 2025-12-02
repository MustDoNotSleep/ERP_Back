package com.erp.controller;

import com.erp.scheduler.AnnualLeaveScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 연차 관리 API
 */
@RestController
@RequestMapping("/annual-leave")
@RequiredArgsConstructor
public class AnnualLeaveController {
    
    private final AnnualLeaveScheduler annualLeaveScheduler;
    
    /**
     * 전체 직원 연차 일괄 발생 (관리자용)
     * POST /api/annual-leave/generate-all
     */
    @PostMapping("/generate-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Map<String, String>> generateAnnualLeaveForAll() {
        try {
            annualLeaveScheduler.generateAnnualLeaveForAllEmployees();
            return ResponseEntity.ok(Map.of(
                "message", "전체 직원 연차 발생이 완료되었습니다.",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "연차 발생 중 오류 발생: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
    
    /**
     * 월별 연차 일괄 발생 (관리자용)
     * POST /api/annual-leave/generate-monthly
     * 임시로 인증 제거 - 테스트 후 다시 활성화 필요
     */
    @PostMapping("/generate-monthly")
    // @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Map<String, String>> generateMonthlyLeaveForAll() {
        try {
            annualLeaveScheduler.generateMonthlyLeaveForNewEmployees();
            return ResponseEntity.ok(Map.of(
                "message", "1년 미만 직원 월별 연차 발생이 완료되었습니다.",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "월별 연차 발생 중 오류 발생: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
}

