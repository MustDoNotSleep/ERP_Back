package com.erp.service;

import com.erp.dto.WelfareDto;
import com.erp.entity.Employee;
import com.erp.entity.Welfare;
import com.erp.entity.enums.WelfareType;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.WelfareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareService {
    
    private final WelfareRepository welfareRepository;
    private final EmployeeRepository employeeRepository;
    
    // 연간 복리후생 예산 (직급별로 다르게 설정 가능, 현재는 고정값)
    private static final BigDecimal ANNUAL_WELFARE_BUDGET = new BigDecimal("2000000"); // 200만원
    
    /**
     * 직원별 복리후생 사용 내역 조회
     */
    public List<WelfareDto.Response> getWelfareByEmployeeId(Long employeeId) {
        List<Welfare> welfares = welfareRepository.findByEmployeeIdOrderByPaymentMonthDesc(employeeId);
        return welfares.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 직원의 복리후생 잔액 조회 (연간 기준)
     */
    public WelfareDto.BalanceResponse getWelfareBalance(Long employeeId, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        // 해당 연도 사용 금액 조회
        BigDecimal usedAmount = welfareRepository.getTotalUsedAmountByEmployeeAndYear(employeeId, String.valueOf(year));
        BigDecimal remainingAmount = ANNUAL_WELFARE_BUDGET.subtract(usedAmount);
        
        return WelfareDto.BalanceResponse.builder()
            .employeeId(employeeId)
            .employeeName(employee.getName())
            .yearMonth(YearMonth.of(year, 1)) // 연도 표시
            .totalBudget(ANNUAL_WELFARE_BUDGET)
            .usedAmount(usedAmount)
            .remainingAmount(remainingAmount)
            .build();
    }
    
    /**
     * 복리후생 사용 신청 (인사팀이 등록)
     */
    @Transactional
    public WelfareDto.Response createWelfare(WelfareDto.Request request, Long approverId) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
        
        Employee approver = employeeRepository.findById(approverId)
            .orElseThrow(() -> new IllegalArgumentException("승인자를 찾을 수 없습니다."));
        
        // 잔액 확인
        String year = String.valueOf(request.getPaymentMonth().getYear());
        BigDecimal usedAmount = welfareRepository.getTotalUsedAmountByEmployeeAndYear(request.getEmployeeId(), year);
        BigDecimal remainingAmount = ANNUAL_WELFARE_BUDGET.subtract(usedAmount);
        
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalStateException("복리후생 예산을 초과했습니다. (남은 예산: " + remainingAmount + "원)");
        }
        
        Welfare welfare = Welfare.builder()
            .employee(employee)
            .welfareType(WelfareType.valueOf(request.getWelfareType()))
            .paymentMonth(request.getPaymentMonth())
            .amount(request.getAmount())
            .paymentDate(request.getPaymentDate())
            .note(request.getNote())
            .approver(approver)
            .isApproved(true) // 인사팀이 직접 등록하므로 자동 승인
            .build();
        
        Welfare saved = welfareRepository.save(welfare);
        return convertToResponse(saved);
    }
    
    /**
     * 승인 대기 중인 복리후생 목록 (미래 확장용)
     */
    public List<WelfareDto.Response> getPendingWelfares() {
        List<Welfare> welfares = welfareRepository.findByIsApprovedFalseOrderByCreatedAtDesc();
        return welfares.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Entity → DTO 변환
     */
    private WelfareDto.Response convertToResponse(Welfare welfare) {
        return WelfareDto.Response.builder()
            .id(welfare.getId())
            .employeeId(welfare.getEmployee().getId())
            .employeeName(welfare.getEmployee().getName())
            .departmentName(welfare.getEmployee().getDepartment() != null 
                ? welfare.getEmployee().getDepartment().getDepartmentName() : null)
            .welfareType(welfare.getWelfareType().name())
            .welfareTypeName(welfare.getWelfareType().getDescription())
            .paymentMonth(welfare.getPaymentMonth())
            .amount(welfare.getAmount())
            .paymentDate(welfare.getPaymentDate())
            .note(welfare.getNote())
            .approverId(welfare.getApprover() != null ? welfare.getApprover().getId() : null)
            .approverName(welfare.getApprover() != null ? welfare.getApprover().getName() : null)
            .isApproved(welfare.getIsApproved())
            .createdAt(welfare.getCreatedAt())
            .build();
    }
}
