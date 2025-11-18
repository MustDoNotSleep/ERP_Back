package com.erp.service;

import com.erp.dto.SalaryDto;
import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.SalaryModification;
import com.erp.entity.enums.SalaryStatus;
import com.erp.entity.enums.UpdateTargetType;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.SalaryRepository;
import com.erp.repository.SalaryInfoRepository;
import com.erp.repository.SalaryModificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryService {
	private final SalaryRepository salaryRepository;
	private final EmployeeRepository employeeRepository;
	private final SalaryInfoRepository salaryInfoRepository;
	private final SalaryModificationRepository salaryModificationRepository;

	/**
	 * 급여 생성
	 * - 세금 및 4대보험이 Request에 포함되어 있으면 해당 값 사용
	 * - 포함되어 있지 않으면 자동 계산 (간이세액표 기반)
	 */
	@Transactional
	public SalaryDto.Response createSalary(SalaryDto.Request request) {
		Employee employee = employeeRepository.findById(request.getEmployeeId())
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
		// SalaryInfo에서 월 기본급 가져와서 연봉 환산
		java.math.BigDecimal baseSalary = null;
		if (employee.getId() != null) {
			var salaryInfoOpt = salaryInfoRepository.findByEmployee(employee);
			if (salaryInfoOpt.isPresent()) {
				java.math.BigDecimal monthlyBase = salaryInfoOpt.get().getMonthlyBaseSalary();
				baseSalary = monthlyBase.multiply(java.math.BigDecimal.valueOf(12));
			}
		}
		if (request.getBaseSalary() != null) {
			baseSalary = request.getBaseSalary(); // request 값이 우선
		}
		Salary salary = Salary.builder()
			.employee(employee)
			.paymentDate(request.getPaymentDate())
			.baseSalary(baseSalary)
			.overtimeAllowance(request.getOvertimeAllowance())
			.nightAllowance(request.getNightAllowance())
			.bonus(request.getBonus())
			// 세금 및 4대보험 (null이면 자동 계산됨)
			.incomeTax(request.getIncomeTax())
			.nationalPension(request.getNationalPension())
			.healthInsurance(request.getHealthInsurance())
			.employmentInsurance(request.getEmploymentInsurance())
			.otherDeductions(request.getOtherDeductions())
			.salaryStatus(request.getSalaryStatus() != null ? request.getSalaryStatus() : SalaryStatus.DRAFT)
			.build();
		// 총 급여 계산 후 실수령액 계산 (세금/보험 자동 계산 포함)
		salary.calculateTotal();
		salary.calculateNetSalary(); // 내부에서 calculateTaxAndInsurance() 호출
		Salary saved = salaryRepository.save(salary);
		return SalaryDto.Response.from(saved);
	}

	/**
	 * 급여 수정
	 * - 세금 및 4대보험이 Request에 포함되어 있으면 해당 값으로 업데이트
	 * - 포함되어 있지 않으면 기존 값 유지 (null로 변경 시 자동 재계산)
	 */
	@Transactional
	public SalaryDto.Response updateSalary(Long id, SalaryDto.Request request) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		// 항목별로 값 업데이트
		if (request.getPaymentDate() != null) salary.setPaymentDate(request.getPaymentDate());
		if (request.getBaseSalary() != null) salary.setBaseSalary(request.getBaseSalary());
		if (request.getOvertimeAllowance() != null) salary.setOvertimeAllowance(request.getOvertimeAllowance());
		if (request.getNightAllowance() != null) salary.setNightAllowance(request.getNightAllowance());
		if (request.getBonus() != null) salary.setBonus(request.getBonus());
		// 세금/보험 업데이트 (값이 있으면 업데이트)
		if (request.getIncomeTax() != null) salary.setIncomeTax(request.getIncomeTax());
		if (request.getNationalPension() != null) salary.setNationalPension(request.getNationalPension());
		if (request.getHealthInsurance() != null) salary.setHealthInsurance(request.getHealthInsurance());
		if (request.getEmploymentInsurance() != null) salary.setEmploymentInsurance(request.getEmploymentInsurance());
		if (request.getOtherDeductions() != null) salary.setOtherDeductions(request.getOtherDeductions());
		if (request.getSalaryStatus() != null) salary.setSalaryStatus(request.getSalaryStatus());
		// 재계산 (세금/보험도 자동 재계산됨)
		salary.calculateTotal();
		salary.calculateNetSalary();
		Salary saved = salaryRepository.save(salary);
		return SalaryDto.Response.from(saved);
	}

	public SalaryDto.Response getSalary(Long id) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		return SalaryDto.Response.from(salary);
	}

	public List<SalaryDto.Response> getEmployeeSalaries(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
		return salaryRepository.findByEmployee(employee).stream()
			.map(SalaryDto.Response::from)
			.collect(Collectors.toList());
	}

	public List<SalaryDto.Response> getMonthlySalaries(YearMonth yearMonth) {
		return salaryRepository.findByPaymentDateWithEmployee(yearMonth)
			.stream()
			.map(SalaryDto.Response::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public void confirmSalary(Long id) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		if (salary.getSalaryStatus() != SalaryStatus.DRAFT) {
			throw new IllegalStateException("급여가 DRAFT 상태가 아닙니다.");
		}
		salary.confirm();
	}

	@Transactional
	public void markAsPaid(Long id) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		if (salary.getSalaryStatus() != SalaryStatus.CONFIRMED) {
			throw new IllegalStateException("급여가 CONFIRMED 상태가 아닙니다.");
		}
		salary.markAsPaid();
	}
	
	/**
	 * 특정 월 전체 직원 급여 일괄 수정
	 * 보너스 등 추가 금액을 일괄 적용할 때 사용
	 */
	@Transactional
	public SalaryDto.BulkUpdateResponse bulkUpdateMonthlySalaries(SalaryDto.BulkUpdateRequest request) {
		YearMonth paymentDate = request.getPaymentDate();
		
		// 해당 월의 모든 급여 조회
		List<Salary> salaries = salaryRepository.findByPaymentDate(paymentDate);
		
		if (salaries.isEmpty()) {
			throw new IllegalArgumentException(paymentDate + "에 해당하는 급여 내역이 없습니다.");
		}
		
		int updatedCount = 0;
		
		for (Salary salary : salaries) {
			boolean updated = false;
			
			// 수당 추가
			if (request.getBonusToAdd() != null && request.getBonusToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setBonus(salary.getBonus().add(request.getBonusToAdd()));
				updated = true;
			}
			
			if (request.getOvertimeAllowanceToAdd() != null && request.getOvertimeAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setOvertimeAllowance(salary.getOvertimeAllowance().add(request.getOvertimeAllowanceToAdd()));
				updated = true;
			}
			
			if (request.getNightAllowanceToAdd() != null && request.getNightAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setNightAllowance(salary.getNightAllowance().add(request.getNightAllowanceToAdd()));
				updated = true;
			}
			
			// 공제 항목 추가
			if (request.getIncomeTaxToAdd() != null && request.getIncomeTaxToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setIncomeTax(salary.getIncomeTax().add(request.getIncomeTaxToAdd()));
				updated = true;
			}
			
			if (request.getNationalPensionToAdd() != null && request.getNationalPensionToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setNationalPension(salary.getNationalPension().add(request.getNationalPensionToAdd()));
				updated = true;
			}
			
			if (request.getHealthInsuranceToAdd() != null && request.getHealthInsuranceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setHealthInsurance(salary.getHealthInsurance().add(request.getHealthInsuranceToAdd()));
				updated = true;
			}
			
			if (request.getEmploymentInsuranceToAdd() != null && request.getEmploymentInsuranceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setEmploymentInsurance(salary.getEmploymentInsurance().add(request.getEmploymentInsuranceToAdd()));
				updated = true;
			}
			
			if (request.getOtherDeductionsToAdd() != null && request.getOtherDeductionsToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setOtherDeductions(salary.getOtherDeductions().add(request.getOtherDeductionsToAdd()));
				updated = true;
			}
			
			// 수정사항이 있으면 총액과 실수령액 재계산
			if (updated) {
				salary.calculateTotal();
				salary.calculateNetSalary();
				salaryRepository.save(salary);
				updatedCount++;
			}
		}
		
		return SalaryDto.BulkUpdateResponse.builder()
			.updatedCount(updatedCount)
			.paymentDate(paymentDate)
			.message(paymentDate + " 급여 " + updatedCount + "건이 일괄 수정되었습니다.")
			.build();
	}
	
	/**
	 * 필터링된 급여 일괄 수정
	 * targetType에 따라 대상 직원을 필터링하여 급여 수정
	 */
	@Transactional
	public SalaryDto.FilteredBulkUpdateResponse bulkUpdateFilteredSalaries(SalaryDto.FilteredBulkUpdateRequest request) {
		YearMonth paymentDate = request.getPaymentDate();
		UpdateTargetType targetType = request.getTargetType();
		
		// 대상 급여 목록 조회
		List<Salary> targetSalaries;
		String targetInfo;
		
		switch (targetType) {
			case ALL:
				// 전체 직원
				targetSalaries = salaryRepository.findByPaymentDate(paymentDate);
				targetInfo = "전체 직원";
				break;
				
			case DEPARTMENT:
				// 특정 부서
				if (request.getTargetDepartment() == null || request.getTargetDepartment().isEmpty()) {
					throw new IllegalArgumentException("부서명을 입력해주세요.");
				}
				targetSalaries = salaryRepository.findByPaymentDateAndDepartment(
					paymentDate, 
					request.getTargetDepartment()
				);
				targetInfo = request.getTargetDepartment();
				break;
				
			case POSITION:
				// 특정 직급
				if (request.getTargetPosition() == null || request.getTargetPosition().isEmpty()) {
					throw new IllegalArgumentException("직급명을 입력해주세요.");
				}
				targetSalaries = salaryRepository.findByPaymentDateAndPosition(
					paymentDate, 
					request.getTargetPosition()
				);
				targetInfo = request.getTargetPosition();
				break;
				
			case EMPLOYEE:
				// 특정 직원
				if (request.getTargetEmployeeId() == null) {
					throw new IllegalArgumentException("직원 ID를 입력해주세요.");
				}
				Employee employee = employeeRepository.findById(request.getTargetEmployeeId())
					.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
				
				Salary salary = salaryRepository.findByEmployeeAndPaymentDate(employee, paymentDate)
					.orElseThrow(() -> new IllegalArgumentException(
						paymentDate + " 해당 직원의 급여 내역을 찾을 수 없습니다."
					));
				targetSalaries = List.of(salary);
				targetInfo = employee.getName();
				break;
				
			default:
				throw new IllegalArgumentException("올바르지 않은 대상 유형입니다: " + targetType);
		}
		
		if (targetSalaries.isEmpty()) {
			return SalaryDto.FilteredBulkUpdateResponse.builder()
				.updatedCount(0)
				.paymentDate(paymentDate)
				.targetType(targetType)
				.targetInfo(targetInfo)
				.message(paymentDate + " 해당하는 급여 내역이 없습니다.")
				.build();
		}
		
		// 일괄 수정 처리
		int updatedCount = 0;
		java.util.List<SalaryDto.Response> updatedSalaries = new java.util.ArrayList<>();
		
		for (Salary salary : targetSalaries) {
			boolean updated = false;
			
			// 수당 추가
			if (request.getBonusToAdd() != null && request.getBonusToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setBonus(
					(salary.getBonus() != null ? salary.getBonus() : java.math.BigDecimal.ZERO)
						.add(request.getBonusToAdd())
				);
				updated = true;
			}
			
			if (request.getOvertimeAllowanceToAdd() != null && request.getOvertimeAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setOvertimeAllowance(
					(salary.getOvertimeAllowance() != null ? salary.getOvertimeAllowance() : java.math.BigDecimal.ZERO)
						.add(request.getOvertimeAllowanceToAdd())
				);
				updated = true;
			}
			
			if (request.getNightAllowanceToAdd() != null && request.getNightAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
				salary.setNightAllowance(
					(salary.getNightAllowance() != null ? salary.getNightAllowance() : java.math.BigDecimal.ZERO)
						.add(request.getNightAllowanceToAdd())
				);
				updated = true;
			}
			
			// 보너스 관련 정보 업데이트
			if (request.getBonusReason() != null && !request.getBonusReason().isEmpty()) {
				salary.setBonusReason(request.getBonusReason());
				updated = true;
			}
			
			if (request.getBonusAttachment() != null && !request.getBonusAttachment().isEmpty()) {
				salary.setBonusAttachment(request.getBonusAttachment());
				updated = true;
			}
			
			// 수정사항이 있으면 총액과 실수령액 재계산 (세금/보험도 자동 재계산)
			if (updated) {
				salary.calculateTotal();
				salary.calculateNetSalary(); // 세금/보험 자동 재계산 포함
				Salary savedSalary = salaryRepository.save(salary);
				updatedSalaries.add(SalaryDto.Response.from(savedSalary));
				updatedCount++;
			}
		}
		
		// ⭐ 보너스 수정 이력 저장 (보너스만 추가된 경우에만)
		if (updatedCount > 0 && request.getBonusToAdd() != null 
			&& request.getBonusToAdd().compareTo(java.math.BigDecimal.ZERO) > 0) {
			
			SalaryModification modification = SalaryModification.builder()
				.paymentDate(paymentDate)
				.targetType(targetType)
				.targetName(targetInfo)
				.employeeCount(updatedCount)
				.amount(request.getBonusToAdd())
				.description(request.getBonusReason())
				.modifiedAt(java.time.LocalDateTime.now())
				.modifiedBy("SYSTEM") // TODO: 실제 사용자 정보로 변경
				.build();
			
			salaryModificationRepository.save(modification);
		}
		
		return SalaryDto.FilteredBulkUpdateResponse.builder()
			.updatedCount(updatedCount)
			.paymentDate(paymentDate)
			.targetType(targetType)
			.targetInfo(targetInfo)
			.updatedSalaries(updatedSalaries)
			.message(targetInfo + " " + updatedCount + "명의 급여가 수정되었습니다.")
			.build();
	}
	
	/**
	 * 급여 삭제
	 */
	@Transactional
	public void deleteSalary(Long id) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		
		// 이미 지급 완료된 급여는 삭제 불가
		if (salary.getSalaryStatus() == SalaryStatus.PAID) {
			throw new IllegalStateException("이미 지급 완료된 급여는 삭제할 수 없습니다.");
		}
		
		salaryRepository.delete(salary);
	}
	
	/**
	 * 예상 월급 조회
	 * 아직 생성되지 않은 월급 정보를 SalaryInfo 기반으로 예상하여 반환
	 * 
	 * @param employeeId 직원 ID
	 * @param targetMonth 조회할 년월
	 * @return 예상 급여 정보
	 */
	public SalaryDto.PreviewResponse getEstimatedSalary(Long employeeId, YearMonth targetMonth) {
		// 직원 정보 조회
		Employee employee = employeeRepository.findById(employeeId)
			.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
		
		// SalaryInfo에서 기본급 조회
		var salaryInfoOpt = salaryInfoRepository.findByEmployee(employee);
		if (salaryInfoOpt.isEmpty()) {
			throw new IllegalArgumentException("직원의 급여 정보(SalaryInfo)가 설정되지 않았습니다.");
		}
		
		java.math.BigDecimal monthlyBaseSalary = salaryInfoOpt.get().getMonthlyBaseSalary();
		
		// 예상 총 급여 계산 (기본급만 사용, 수당은 0으로 가정)
		java.math.BigDecimal overtimeAllowance = java.math.BigDecimal.ZERO;
		java.math.BigDecimal nightAllowance = java.math.BigDecimal.ZERO;
		java.math.BigDecimal bonus = java.math.BigDecimal.ZERO;
		
		java.math.BigDecimal totalSalary = monthlyBaseSalary
			.add(overtimeAllowance)
			.add(nightAllowance)
			.add(bonus);
		
		// 세금 및 4대보험 자동 계산
		java.math.BigDecimal incomeTax = com.erp.util.TaxCalculator.calculateIncomeTax(totalSalary);
		java.math.BigDecimal localIncomeTax = com.erp.util.TaxCalculator.calculateLocalIncomeTax(incomeTax);
		java.math.BigDecimal nationalPension = com.erp.util.TaxCalculator.calculateNationalPension(totalSalary);
		java.math.BigDecimal healthInsurance = com.erp.util.TaxCalculator.calculateHealthInsurance(totalSalary);
		java.math.BigDecimal employmentInsurance = com.erp.util.TaxCalculator.calculateEmploymentInsurance(totalSalary);
		
		java.math.BigDecimal totalDeductions = incomeTax
			.add(localIncomeTax)
			.add(nationalPension)
			.add(healthInsurance)
			.add(employmentInsurance);
		
		java.math.BigDecimal netSalary = totalSalary.subtract(totalDeductions);
		
		// 응답 DTO 생성
		return SalaryDto.PreviewResponse.builder()
			.employeeId(employee.getId())
			.employeeName(employee.getName())
			.departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
			.positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
			.paymentDate(targetMonth)
			.baseSalary(monthlyBaseSalary)
			.overtimeAllowance(overtimeAllowance)
			.nightAllowance(nightAllowance)
			.bonus(bonus)
			.totalSalary(totalSalary)
			.incomeTax(incomeTax)
			.localIncomeTax(localIncomeTax)
			.nationalPension(nationalPension)
			.healthInsurance(healthInsurance)
			.employmentInsurance(employmentInsurance)
			.totalDeductions(totalDeductions)
			.netSalary(netSalary)
			.message(targetMonth.getYear() + "년 " + targetMonth.getMonthValue() + "월 예상 급여")
			.isEstimated(true)
			.build();
	}
	
	/**
	 * 전체 직원 예상 월급 조회
	 * SalaryInfo 기반으로 모든 직원의 예상 월급을 계산하여 반환
	 * 
	 * @param targetMonth 조회할 년월
	 * @return 전체 직원의 예상 급여 정보 리스트
	 */
	public List<SalaryDto.PreviewResponse> getAllEstimatedSalaries(YearMonth targetMonth) {
		// 전체 직원 조회
		List<Employee> allEmployees = employeeRepository.findAll();
		
		List<SalaryDto.PreviewResponse> previewList = new java.util.ArrayList<>();
		
		for (Employee employee : allEmployees) {
			try {
				// SalaryInfo에서 기본급 조회
				var salaryInfoOpt = salaryInfoRepository.findByEmployee(employee);
				if (salaryInfoOpt.isEmpty()) {
					// SalaryInfo 없으면 스킵
					continue;
				}
				
				java.math.BigDecimal monthlyBaseSalary = salaryInfoOpt.get().getMonthlyBaseSalary();
				
				// 예상 총 급여 계산 (기본급만 사용, 수당은 0으로 가정)
				java.math.BigDecimal overtimeAllowance = java.math.BigDecimal.ZERO;
				java.math.BigDecimal nightAllowance = java.math.BigDecimal.ZERO;
				java.math.BigDecimal bonus = java.math.BigDecimal.ZERO;
				
				java.math.BigDecimal totalSalary = monthlyBaseSalary
					.add(overtimeAllowance)
					.add(nightAllowance)
					.add(bonus);
				
				// 세금 및 4대보험 자동 계산
				java.math.BigDecimal incomeTax = com.erp.util.TaxCalculator.calculateIncomeTax(totalSalary);
				java.math.BigDecimal localIncomeTax = com.erp.util.TaxCalculator.calculateLocalIncomeTax(incomeTax);
				java.math.BigDecimal nationalPension = com.erp.util.TaxCalculator.calculateNationalPension(totalSalary);
				java.math.BigDecimal healthInsurance = com.erp.util.TaxCalculator.calculateHealthInsurance(totalSalary);
				java.math.BigDecimal employmentInsurance = com.erp.util.TaxCalculator.calculateEmploymentInsurance(totalSalary);
				
				java.math.BigDecimal totalDeductions = incomeTax
					.add(localIncomeTax)
					.add(nationalPension)
					.add(healthInsurance)
					.add(employmentInsurance);
				
				java.math.BigDecimal netSalary = totalSalary.subtract(totalDeductions);
				
				// 응답 DTO 생성
				SalaryDto.PreviewResponse preview = SalaryDto.PreviewResponse.builder()
					.employeeId(employee.getId())
					.employeeName(employee.getName())
					.departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
					.positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
					.paymentDate(targetMonth)
					.baseSalary(monthlyBaseSalary)
					.overtimeAllowance(overtimeAllowance)
					.nightAllowance(nightAllowance)
					.bonus(bonus)
					.totalSalary(totalSalary)
					.incomeTax(incomeTax)
					.localIncomeTax(localIncomeTax)
					.nationalPension(nationalPension)
					.healthInsurance(healthInsurance)
					.employmentInsurance(employmentInsurance)
					.totalDeductions(totalDeductions)
					.netSalary(netSalary)
					.message(targetMonth.getYear() + "년 " + targetMonth.getMonthValue() + "월 예상 급여")
					.isEstimated(true)
					.build();
				
				previewList.add(preview);
				
			} catch (Exception e) {
				// 개별 직원 처리 중 오류 발생 시 스킵
				continue;
			}
		}
		
		return previewList;
	}
	
	/**
	 * 월별 일괄 생성/수정(Upsert) - 통합 API
	 * - 급여가 없으면 생성: SalaryInfo 기본급 기준 + 근태 데이터(선택)
	 * - 급여가 있으면 수정: ToAdd 방식으로 추가
	 */
	@Transactional
	public SalaryDto.MonthlyUpsertResponse upsertMonthlySalaries(SalaryDto.MonthlyUpsertRequest request) {
		YearMonth paymentDate = request.getPaymentDate();
		UpdateTargetType targetType = request.getTargetType();
		
		// 대상 직원 목록 조회
		List<Employee> targetEmployees;
		String targetInfo;
		
		switch (targetType) {
			case ALL:
				// 전체 직원
				targetEmployees = employeeRepository.findAll();
				targetInfo = "전체 직원";
				break;
				
			case DEPARTMENT:
				// 특정 부서
				if (request.getTargetDepartment() == null || request.getTargetDepartment().isEmpty()) {
					throw new IllegalArgumentException("부서명을 입력해주세요.");
				}
				targetEmployees = employeeRepository.findByDepartment_DepartmentName(request.getTargetDepartment());
				targetInfo = request.getTargetDepartment();
				break;
				
			case POSITION:
				// 특정 직급
				if (request.getTargetPosition() == null || request.getTargetPosition().isEmpty()) {
					throw new IllegalArgumentException("직급명을 입력해주세요.");
				}
				targetEmployees = employeeRepository.findByPosition_PositionName(request.getTargetPosition());
				targetInfo = request.getTargetPosition();
				break;
				
			case EMPLOYEE:
				// 특정 직원
				if (request.getTargetEmployeeId() == null) {
					throw new IllegalArgumentException("직원 ID를 입력해주세요.");
				}
				Employee employee = employeeRepository.findById(request.getTargetEmployeeId())
					.orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다."));
				targetEmployees = List.of(employee);
				targetInfo = employee.getName();
				break;
				
			default:
				throw new IllegalArgumentException("올바르지 않은 대상 유형입니다: " + targetType);
		}
		
		if (targetEmployees.isEmpty()) {
			return SalaryDto.MonthlyUpsertResponse.builder()
				.createdCount(0)
				.updatedCount(0)
				.paymentDate(paymentDate)
				.targetType(targetType)
				.targetInfo(targetInfo)
				.message("대상 직원이 없습니다.")
				.build();
		}
		
		int createdCount = 0;
		int updatedCount = 0;
		java.util.List<SalaryDto.Response> createdSalaries = new java.util.ArrayList<>();
		java.util.List<SalaryDto.Response> updatedSalaries = new java.util.ArrayList<>();
		
		// 각 직원별로 Upsert 처리
		for (Employee employee : targetEmployees) {
			var existingSalaryOpt = salaryRepository.findByEmployeeAndPaymentDate(employee, paymentDate);
			
			if (existingSalaryOpt.isPresent()) {
				// 급여 존재 → 수정 (기존 bulkUpdate 로직 재사용)
				Salary salary = existingSalaryOpt.get();
				boolean updated = false;
				
				// 수당 추가
				if (request.getBonusToAdd() != null && request.getBonusToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
					salary.setBonus(
						(salary.getBonus() != null ? salary.getBonus() : java.math.BigDecimal.ZERO)
							.add(request.getBonusToAdd())
					);
					updated = true;
				}
				
				if (request.getOvertimeAllowanceToAdd() != null && request.getOvertimeAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
					salary.setOvertimeAllowance(
						(salary.getOvertimeAllowance() != null ? salary.getOvertimeAllowance() : java.math.BigDecimal.ZERO)
							.add(request.getOvertimeAllowanceToAdd())
					);
					updated = true;
				}
				
				if (request.getNightAllowanceToAdd() != null && request.getNightAllowanceToAdd().compareTo(java.math.BigDecimal.ZERO) != 0) {
					salary.setNightAllowance(
						(salary.getNightAllowance() != null ? salary.getNightAllowance() : java.math.BigDecimal.ZERO)
							.add(request.getNightAllowanceToAdd())
					);
					updated = true;
				}
				
				// 보너스 관련 정보 업데이트
				if (request.getBonusReason() != null && !request.getBonusReason().isEmpty()) {
					salary.setBonusReason(request.getBonusReason());
					updated = true;
				}
				
				if (request.getBonusAttachment() != null && !request.getBonusAttachment().isEmpty()) {
					salary.setBonusAttachment(request.getBonusAttachment());
					updated = true;
				}
				
				if (updated) {
					// 재계산 (세금/보험 자동 재계산)
					salary.calculateTotal();
					salary.calculateNetSalary();
					Salary savedSalary = salaryRepository.save(salary);
					updatedSalaries.add(SalaryDto.Response.from(savedSalary));
					updatedCount++;
				}
				
			} else {
				// 급여 없음 → 생성 (Preview 로직 + Attendance 데이터)
				var salaryInfoOpt = salaryInfoRepository.findByEmployee(employee);
				if (salaryInfoOpt.isEmpty()) {
					// SalaryInfo 없으면 스킵 (기본급 정보 없음)
					continue;
				}
				
				java.math.BigDecimal monthlyBaseSalary = salaryInfoOpt.get().getMonthlyBaseSalary();
				
				// 근태 데이터 사용 여부
				java.math.BigDecimal overtimeAllowance = java.math.BigDecimal.ZERO;
				java.math.BigDecimal nightAllowance = java.math.BigDecimal.ZERO;
				
				// TODO: useAttendanceData == true일 경우 Attendance에서 조회
				// 현재는 간단히 0으로 처리 (추후 AttendanceRepository 연동)
				
				java.math.BigDecimal bonus = request.getDefaultBonus() != null 
					? request.getDefaultBonus() 
					: java.math.BigDecimal.ZERO;
				
				// 급여 생성
				Salary newSalary = Salary.builder()
					.employee(employee)
					.paymentDate(paymentDate)
					.baseSalary(monthlyBaseSalary)
					.overtimeAllowance(overtimeAllowance)
					.nightAllowance(nightAllowance)
					.bonus(bonus)
					.bonusReason(request.getBonusReason())  // 보너스 사유
					.bonusAttachment(request.getBonusAttachment())  // 첨부파일
					.incomeTax(null)  // 자동 계산
					.nationalPension(null)  // 자동 계산
					.healthInsurance(null)  // 자동 계산
					.employmentInsurance(null)  // 자동 계산
					.otherDeductions(java.math.BigDecimal.ZERO)
					.salaryStatus(request.getSalaryStatus() != null 
						? request.getSalaryStatus() 
						: SalaryStatus.DRAFT)
					.build();
				
				// 자동 계산
				newSalary.calculateTotal();
				newSalary.calculateNetSalary();
				
				Salary savedSalary = salaryRepository.save(newSalary);
				createdSalaries.add(SalaryDto.Response.from(savedSalary));
				createdCount++;
			}
		}
		
		// ⭐ 보너스 수정 이력 저장 (보너스만 추가된 경우에만)
		if (updatedCount > 0 && request.getBonusToAdd() != null 
			&& request.getBonusToAdd().compareTo(java.math.BigDecimal.ZERO) > 0) {
			
			SalaryModification modification = SalaryModification.builder()
				.paymentDate(paymentDate)
				.targetType(targetType)
				.targetName(targetInfo)
				.employeeCount(updatedCount)
				.amount(request.getBonusToAdd())
				.description(request.getBonusReason())
				.modifiedAt(java.time.LocalDateTime.now())
				.modifiedBy("SYSTEM") // TODO: 실제 사용자 정보로 변경
				.build();
			
			salaryModificationRepository.save(modification);
		}
		
		return SalaryDto.MonthlyUpsertResponse.builder()
			.createdCount(createdCount)
			.updatedCount(updatedCount)
			.paymentDate(paymentDate)
			.targetType(targetType)
			.targetInfo(targetInfo)
			.createdSalaries(createdSalaries)
			.updatedSalaries(updatedSalaries)
			.message(String.format(
				"%s의 %s 급여 처리 완료 (생성: %d건, 수정: %d건)", 
				targetInfo, 
				paymentDate, 
				createdCount, 
				updatedCount
			))
			.build();
	}
	
	/**
	 * 급여 수정 내역 조회 (요약 정보)
	 * SalaryModification 테이블에서 수정 이력 조회
	 */
	public SalaryDto.ModificationListResponse getSalaryModifications(YearMonth yearMonth) {
		// 해당 월의 수정 이력 조회
		List<SalaryModification> modifications = salaryModificationRepository.findByPaymentDateOrderByModifiedAtDesc(yearMonth);
		
		List<SalaryDto.ModificationResponse> modificationResponses = modifications.stream()
			.map(mod -> {
				// targetType을 문자열로 변환
				String typeStr = switch (mod.getTargetType()) {
					case ALL -> "전체";
					case DEPARTMENT -> "부서별";
					case POSITION -> "직급별";
					case EMPLOYEE -> "개인별";
				};
				
				return SalaryDto.ModificationResponse.builder()
					.id(mod.getId())
					.paymentDate(mod.getPaymentDate())
					.type(typeStr)
					.targetName(mod.getTargetName())
					.employeeCount(mod.getEmployeeCount())
					.amount(mod.getAmount())
					.description(mod.getDescription())
					.createdAt(mod.getModifiedAt())
					.createdBy(mod.getModifiedBy())
					.build();
			})
			.collect(Collectors.toList());
		
		return SalaryDto.ModificationListResponse.builder()
			.yearMonth(yearMonth)
			.totalModifications(modificationResponses.size())
			.modifications(modificationResponses)
			.build();
	}
}