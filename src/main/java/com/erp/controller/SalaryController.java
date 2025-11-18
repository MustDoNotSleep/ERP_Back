package com.erp.controller;

import com.erp.dto.SalaryDto;
import com.erp.service.SalaryService;
import com.erp.util.TaxCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> createSalary(@RequestBody SalaryDto.Request request) {
        SalaryDto.Response response = salaryService.createSalary(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> getSalary(@PathVariable Long id) {
        SalaryDto.Response response = salaryService.getSalary(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<List<SalaryDto.Response>> getEmployeeSalaries(@PathVariable Long employeeId) {
        List<SalaryDto.Response> responses = salaryService.getEmployeeSalaries(employeeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/month/{yearMonth}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<SalaryDto.Response>> getMonthlySalaries(@PathVariable String yearMonth) {
        YearMonth parsedYearMonth = YearMonth.parse(yearMonth);
        List<SalaryDto.Response> responses = salaryService.getMonthlySalaries(parsedYearMonth);
        return ResponseEntity.ok(responses);
    }


    @PutMapping("/{id}") // 급여 정보 수정
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.Response> putSalary(@PathVariable Long id, @RequestBody SalaryDto.Request request) {
        SalaryDto.Response response = salaryService.updateSalary(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm") // 급여 확정 처리
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> confirmSalary(@PathVariable Long id) {
        salaryService.confirmSalary(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pay") // 급여 지급 처리
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        salaryService.markAsPaid(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 특정 월 전체 직원 급여 일괄 수정
     * 보너스 등 추가 금액을 일괄 적용
     * 
     * @param request 일괄 수정 요청 (paymentDate, bonusToAdd 등)
     * @return 수정된 급여 건수와 메시지
     */
    @PutMapping("/bulk-update")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.BulkUpdateResponse> bulkUpdateSalaries(@RequestBody SalaryDto.BulkUpdateRequest request) {
        SalaryDto.BulkUpdateResponse response = salaryService.bulkUpdateMonthlySalaries(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 필터링된 급여 일괄 수정 ⭐ (프론트엔드 요청)
     * targetType에 따라 대상 직원 필터링 후 급여 일괄 수정
     * 
     * @param request 필터링 조건 및 추가 금액 (targetType, bonusToAdd 등)
     * @return 수정된 급여 건수와 메시지
     */
    @PutMapping("/bulk-update-filtered")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.FilteredBulkUpdateResponse> bulkUpdateFilteredSalaries(
        @RequestBody SalaryDto.FilteredBulkUpdateRequest request
    ) {
        SalaryDto.FilteredBulkUpdateResponse response = salaryService.bulkUpdateFilteredSalaries(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 월별 급여 일괄 생성/수정 (Upsert) - 통합 API ⭐ NEW
     * - 급여가 없으면 생성: SalaryInfo 기본급 기준 + 근태 데이터(선택)
     * - 급여가 있으면 수정: ToAdd 방식으로 추가
     * 프론트에서 하나의 API로 통일하여 사용 가능
     * 
     * @param request 대상 조건, 기본값, 추가 금액
     * @return 생성/수정된 급여 건수와 메시지
     */
    // @PostMapping("/bulk-upsert-monthly")
    // @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    // public ResponseEntity<SalaryDto.MonthlyUpsertResponse> upsertMonthlySalaries(
    //     @RequestBody SalaryDto.MonthlyUpsertRequest request
    // ) {
    //     SalaryDto.MonthlyUpsertResponse response = salaryService.upsertMonthlySalaries(request);
    //     return ResponseEntity.ok(response);
    // }
    
    /**
     * 급여 삭제
     * PAID 상태가 아닌 급여만 삭제 가능
     * 
     * @param id 급여 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 급여 수정 내역 조회 (간결한 정보만) ⭐ NEW
     * 월별로 보너스가 수정된 급여 내역만 조회
     * - 대상 유형 (전체/부서/직급/개인)
     * - 보너스 금액, 사유, 첨부파일
     * 
     * @param yearMonth 조회할 년월 (형식: yyyy-MM)
     * @return 수정 내역 목록
     */
    @GetMapping("/modifications/{yearMonth}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<SalaryDto.ModificationListResponse> getSalaryModifications(
        @PathVariable String yearMonth
    ) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);
        SalaryDto.ModificationListResponse response = salaryService.getSalaryModifications(targetMonth);
        return ResponseEntity.ok(response);
    }
    /**
     * 세금 및 4대보험 자동 계산 미리보기
     * 총 급여액을 입력하면 세금/보험이 얼마나 나가는지 확인 가능
     * 
     * @param totalSalary 월 총 급여액
     * @return 계산된 세금 및 보험료, 실수령액
     */
    @GetMapping("/calculate-preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'USER')")
    public ResponseEntity<Map<String, Object>> calculateTaxPreview(@RequestParam BigDecimal totalSalary) {
        Map<String, Object> result = new HashMap<>();
        
        BigDecimal incomeTax = TaxCalculator.calculateIncomeTax(totalSalary);
        BigDecimal localIncomeTax = TaxCalculator.calculateLocalIncomeTax(incomeTax);
        BigDecimal nationalPension = TaxCalculator.calculateNationalPension(totalSalary);
        BigDecimal healthInsurance = TaxCalculator.calculateHealthInsurance(totalSalary);
        BigDecimal employmentInsurance = TaxCalculator.calculateEmploymentInsurance(totalSalary);
        
        BigDecimal totalDeductions = incomeTax
            .add(localIncomeTax)
            .add(nationalPension)
            .add(healthInsurance)
            .add(employmentInsurance);
        
        BigDecimal netSalary = totalSalary.subtract(totalDeductions);
        
        result.put("totalSalary", totalSalary);
        result.put("incomeTax", incomeTax);
        result.put("localIncomeTax", localIncomeTax);
        result.put("nationalPension", nationalPension);
        result.put("healthInsurance", healthInsurance);
        result.put("employmentInsurance", employmentInsurance);
        result.put("totalDeductions", totalDeductions);
        result.put("netSalary", netSalary);
        result.put("deductionRate", totalDeductions.divide(totalSalary, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100")) + "%");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 특정 직원의 예상 월급 조회 (이번 달 급여가 아직 생성되지 않았을 때)
     * SalaryInfo의 기본급을 기준으로 예상 월급을 계산하여 반환
     * 
     * @param employeeId 직원 ID
     * @param yearMonth 조회할 년월 (선택사항, 없으면 현재 월)
     * @return 예상 월급 정보
     */
    @GetMapping("/employee/{employeeId}/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER') or #employeeId == authentication.principal.id")
    public ResponseEntity<SalaryDto.PreviewResponse> getEstimatedSalary(
        @PathVariable Long employeeId,
        @RequestParam(required = false) String yearMonth
    ) {
        YearMonth targetMonth = yearMonth != null ? YearMonth.parse(yearMonth) : YearMonth.now();
        SalaryDto.PreviewResponse response = salaryService.getEstimatedSalary(employeeId, targetMonth);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 전체 직원의 예상 월급 조회 ⭐ NEW
     * 아직 생성되지 않은 월의 전체 직원 예상 월급을 SalaryInfo 기반으로 계산
     * 
     * @param yearMonth 조회할 년월 (형식: yyyy-MM, 선택사항, 없으면 현재 월)
     * @return 전체 직원의 예상 월급 정보 리스트
     */
    @GetMapping("/preview/{yearMonth}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<SalaryDto.PreviewResponse>> getAllEstimatedSalaries(
        @PathVariable String yearMonth
    ) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);
        List<SalaryDto.PreviewResponse> response = salaryService.getAllEstimatedSalaries(targetMonth);
        return ResponseEntity.ok(response);
    }
    
}
