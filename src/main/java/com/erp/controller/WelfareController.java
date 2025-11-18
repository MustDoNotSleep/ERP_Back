package com.erp.controller;

import com.erp.dto.WelfareDto;
import com.erp.service.WelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/welfare")
@RequiredArgsConstructor
public class WelfareController {
    
    private final WelfareService welfareService;
    
    /**
     * 직원별 복리후생 사용 내역 조회
     * 본인 또는 인사팀만 조회 가능
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<List<WelfareDto.Response>> getWelfareByEmployeeId(@PathVariable Long employeeId) {
        List<WelfareDto.Response> welfares = welfareService.getWelfareByEmployeeId(employeeId);
        return ResponseEntity.ok(welfares);
    }
    
    /**
     * 직원의 복리후생 잔액 조회
     * 본인 또는 인사팀만 조회 가능
     */
    @GetMapping("/employee/{employeeId}/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<WelfareDto.BalanceResponse> getWelfareBalance(
        @PathVariable Long employeeId,
        @RequestParam(required = false) Integer year
    ) {
        // year 파라미터가 없으면 현재 연도 사용
        int targetYear = (year != null) ? year : Year.now().getValue();
        WelfareDto.BalanceResponse balance = welfareService.getWelfareBalance(employeeId, targetYear);
        return ResponseEntity.ok(balance);
    }
    
    /**
     * 복리후생 사용 신청 (인사팀이 직접 등록)
     * 교육비, 도서비 등을 직원에게 지급할 때 사용
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<WelfareDto.Response> createWelfare(
        @RequestBody WelfareDto.Request request,
        Authentication authentication
    ) {
        // 현재 로그인한 인사팀 직원이 승인자
        Long approverId = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
            .getUsername() != null ? Long.parseLong(authentication.getName()) : null;
        
        WelfareDto.Response created = welfareService.createWelfare(request, approverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * 승인 대기 중인 복리후생 목록 (미래 확장용)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<WelfareDto.Response>> getPendingWelfares() {
        List<WelfareDto.Response> welfares = welfareService.getPendingWelfares();
        return ResponseEntity.ok(welfares);
    }
}
