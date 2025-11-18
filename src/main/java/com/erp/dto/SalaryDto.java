package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
import com.erp.entity.enums.UpdateTargetType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

public class SalaryDto {
	@Getter
	@Builder
	public static class Request {
		private Long employeeId;
		
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;
		
		private BigDecimal baseSalary; // 기본급
		private BigDecimal overtimeAllowance; // 야근수당
		private BigDecimal nightAllowance; // 야간수당
		private BigDecimal bonus;// 보너스
		
		// 세금 및 4대보험 (선택사항 - null이면 자동 계산됨)
		private BigDecimal incomeTax; // 소득세 (자동계산: 간이세액표 기준)
		private BigDecimal nationalPension; // 국민연금 (자동계산: 4.5%)
		private BigDecimal healthInsurance; // 건강보험 (자동계산: 3.545% + 장기요양 0.4591%)
		private BigDecimal employmentInsurance; // 고용보험 (자동계산: 0.9%)
		private BigDecimal otherDeductions; // 기타 공제 (수동 입력만 가능)
		private SalaryStatus salaryStatus;
	}

	@Getter
	@Builder
	public static class Response {
		private Long id;
		private Long employeeId;
		private String employeeName;
		private String departmentName;
		private String positionName;
		private String teamName;
		
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;
		
		private BigDecimal baseSalary; // 기본급
		private BigDecimal overtimeAllowance; // 야근수당
		private BigDecimal nightAllowance; // 야간수당
		private BigDecimal bonus;// 보너스
		private String bonusReason; // 보너스 지급 사유
		private String bonusAttachment; // 보너스 첨부파일
		private BigDecimal incomeTax; // 소득세
		private BigDecimal localIncomeTax; // 지방소득세 (소득세의 10% 자동계산)
		private BigDecimal nationalPension; // 국민연금
		private BigDecimal healthInsurance; // 건강보험
		private BigDecimal employmentInsurance; // 고용보험
		private BigDecimal otherDeductions; // 기타 공제
		private BigDecimal totalSalary; // 총 급여
		private BigDecimal netSalary; // 실수령액
		private SalaryStatus salaryStatus;

		public static Response from(Salary salary) {
			Employee employee = salary.getEmployee();
			return Response.builder()
				.id(salary.getId())
				.employeeId(employee != null ? employee.getId() : null)
				.employeeName(employee != null ? employee.getName() : null)
				.departmentName(employee != null && employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
				.positionName(employee != null && employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
				.teamName(employee != null && employee.getDepartment() != null ? employee.getDepartment().getTeamName() : null)
				.paymentDate(salary.getPaymentDate())
				.baseSalary(salary.getBaseSalary())
				.overtimeAllowance(salary.getOvertimeAllowance())
				.nightAllowance(salary.getNightAllowance())
				.bonus(salary.getBonus())
				.bonusReason(salary.getBonusReason())
				.bonusAttachment(salary.getBonusAttachment())
				.incomeTax(salary.getIncomeTax())
				.localIncomeTax(salary.getLocalIncomeTax())
				.nationalPension(salary.getNationalPension())
				.healthInsurance(salary.getHealthInsurance())
				.employmentInsurance(salary.getEmploymentInsurance())
				.otherDeductions(salary.getOtherDeductions())
				.totalSalary(salary.getTotalSalary())
				.netSalary(salary.getNetSalary())
				.salaryStatus(salary.getSalaryStatus())
				.build();
		}
	}
	
	// 특정 월 전체 급여 일괄 수정용 DTO
	@Getter
	@Builder
	public static class BulkUpdateRequest {
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;
		
		// 추가할 금액들 (null이면 해당 항목은 수정 안함)
		private BigDecimal bonusToAdd;              // 보너스 추가
		private BigDecimal overtimeAllowanceToAdd;  // 야근수당 추가
		private BigDecimal nightAllowanceToAdd;     // 야간수당 추가
		
		// 공제 항목 추가
		private BigDecimal incomeTaxToAdd; // 소득세 추가
		private BigDecimal nationalPensionToAdd; // 국민연금 추가
		private BigDecimal healthInsuranceToAdd; // 건강보험 추가
		private BigDecimal employmentInsuranceToAdd; // 고용보험 추가
		private BigDecimal otherDeductionsToAdd; // 기타 공제 추가
	}
	
	@Getter
	@Builder
	public static class BulkUpdateResponse {
		private int updatedCount;
		private String message;
		private YearMonth paymentDate;
	}
	
	// 필터링된 급여 일괄 수정용 DTO
	@Getter
	@Builder
	public static class FilteredBulkUpdateRequest {
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;           // 대상 월
		
		private UpdateTargetType targetType;      // 대상 유형: ALL, DEPARTMENT, POSITION, EMPLOYEE
		private String targetDepartment;          // 부서별일 경우
		private String targetPosition;            // 직급별일 경우
		private Long targetEmployeeId;            // 개인별일 경우
		
		// 추가할 금액들 (null이면 해당 항목은 수정 안함)
		private BigDecimal bonusToAdd;                // 보너스 추가
		private BigDecimal overtimeAllowanceToAdd;    // 야근수당 추가
		private BigDecimal nightAllowanceToAdd;       // 야간수당 추가
		
		// 보너스 관련 추가 정보
		private String bonusReason;                   // 보너스 지급 사유
		private String bonusAttachment;               // 첨부파일 경로/URL
	}
	
	@Getter
	@Builder
	public static class FilteredBulkUpdateResponse {
		private int updatedCount;
		private String message;
		private YearMonth paymentDate;
		private UpdateTargetType targetType;
		private String targetInfo; // 대상 정보 (부서명, 직급명, 직원명 등)
		private String bonusReason; // 보너스 지급 사유
		private String bonusAttachment; // 보너스 첨부파일
		private java.util.List<Response> updatedSalaries; // 수정된 급여 상세 목록
	}
	
	/**
	 * 예상 월급 조회 응답
	 * 아직 생성되지 않은 월급 정보를 SalaryInfo 기반으로 예상하여 반환
	 */
	@Getter
	@Builder
	public static class PreviewResponse {
		private Long employeeId;
		private String employeeName;
		private String departmentName;
		private String positionName;
		
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;
		
		// 급여 구성
		private BigDecimal baseSalary;          // 기본급 (SalaryInfo에서)
		private BigDecimal overtimeAllowance;   // 예상 야근수당 (0으로 설정)
		private BigDecimal nightAllowance;      // 예상 야간수당 (0으로 설정)
		private BigDecimal bonus;               // 예상 보너스 (0으로 설정)
		private BigDecimal totalSalary;         // 총 급여
		
		// 공제 항목 (자동 계산)
		private BigDecimal incomeTax;           // 소득세
		private BigDecimal localIncomeTax;      // 지방소득세 (소득세의 10%)
		private BigDecimal nationalPension;     // 국민연금
		private BigDecimal healthInsurance;     // 건강보험 (장기요양보험 포함)
		private BigDecimal employmentInsurance; // 고용보험
		private BigDecimal totalDeductions;     // 총 공제액
		
		// 최종 금액
		private BigDecimal netSalary;           // 실수령액
		
		private String message;                 // "2025년 1월 예상 급여" 형태
		private boolean isEstimated;            // true (예상 급여임을 표시)
	}
	
	/**
	 * 월별 일괄 생성/수정(Upsert) 요청
	 * - 급여가 없으면 생성 (근태 정보가 없어도 기본급 기준으로 생성)
	 * - 급여가 있으면 수정 (ToAdd 방식으로 추가)
	 */
	@Getter
	@Builder
	public static class MonthlyUpsertRequest {
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;           // 대상 월
		
		private UpdateTargetType targetType;      // 대상 유형: ALL, DEPARTMENT, POSITION, EMPLOYEE
		private String targetDepartment;          // 부서별일 경우
		private String targetPosition;            // 직급별일 경우
		private Long targetEmployeeId;            // 개인별일 경우
		
		// 생성시 기본값 (null이면 0으로 처리)
		private BigDecimal defaultBonus;          // 신규 생성시 보너스 (기본 0)
		
		// 수정시 추가할 금액들 (기존 레코드에 대해)
		private BigDecimal bonusToAdd;            // 보너스 추가
		private BigDecimal overtimeAllowanceToAdd; // 야근수당 추가
		private BigDecimal nightAllowanceToAdd;    // 야간수당 추가
		
		// 급여 상태 (신규 생성시 적용)
		private SalaryStatus salaryStatus;        // 기본: DRAFT (임시저장)
		
		// 근태 데이터 사용 여부 (신규 생성시)
		private Boolean useAttendanceData;        // true면 근태에서 야근/야간 가져옴, false면 0
		
		// 보너스 관련 추가 정보
		private String bonusReason;               // 보너스 지급 사유
		private String bonusAttachment;           // 첨부파일 경로/URL
	}
	
	/**
	 * 월별 일괄 생성/수정(Upsert) 응답
	 */
	@Getter
	@Builder
	public static class MonthlyUpsertResponse {
		private int createdCount;                // 신규 생성된 급여 수
		private int updatedCount;                // 수정된 급여 수
		private String message;
		private YearMonth paymentDate;
		private UpdateTargetType targetType;
		private String targetInfo;               // 대상 정보 (부서명, 직급명, 직원명 등)
		private java.util.List<Response> createdSalaries; // 신규 생성된 급여 목록
		private java.util.List<Response> updatedSalaries; // 수정된 급여 목록
	}
	
	/**
	 * 급여 수정 내역 조회용 DTO (간결한 정보만)
	 */
	@Getter
	@Builder
	public static class ModificationResponse {
		private Long id;                         // 수정 내역 고유 ID
		
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth paymentDate;           // 지급 월
		
		private String type;                     // 수정 대상 유형 (전체/부서별/직급별/개인별)
		private String targetName;               // 대상 이름 (전체 직원/개발팀/대리/홍길동)
		private Integer employeeCount;           // 영향받은 직원 수
		private BigDecimal amount;               // 추가된 금액
		private String description;              // 수정 사유
		
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private java.time.LocalDateTime createdAt;    // 수정 일시
		private String createdBy;                // 수정한 사람 (선택)
	}
	
	/**
	 * 급여 수정 내역 목록 조회 응답
	 */
	@Getter
	@Builder
	public static class ModificationListResponse {
		@JsonFormat(pattern = "yyyy-MM")
		private YearMonth yearMonth;
		
		private int totalModifications;          // 전체 수정 건수
		private java.util.List<ModificationResponse> modifications; // 수정 내역 목록
	}
}
