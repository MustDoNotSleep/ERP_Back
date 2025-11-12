package com.erp.service;

import com.erp.dto.SalaryDto;
import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.SalaryRepository;
import com.erp.repository.SalaryInfoRepository;
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
			.dutyAllowance(request.getDutyAllowance())
			.bonus(request.getBonus())
			.incomeTax(request.getIncomeTax())
			.nationalPension(request.getNationalPension())
			.healthInsurance(request.getHealthInsurance())
			.employmentInsurance(request.getEmploymentInsurance())
			.societyFee(request.getSocietyFee())
			.advancePayment(request.getAdvancePayment())
			.otherDeductions(request.getOtherDeductions())
			.salaryStatus(request.getSalaryStatus() != null ? request.getSalaryStatus() : SalaryStatus.DRAFT)
			.build();
		salary.calculateTotal();
		salary.calculateNetSalary();
		Salary saved = salaryRepository.save(salary);
		return SalaryDto.Response.from(saved);
	}

	@Transactional
	public SalaryDto.Response updateSalary(Long id, SalaryDto.Request request) {
		Salary salary = salaryRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("급여 내역을 찾을 수 없습니다."));
		// 항목별로 값 업데이트
		if (request.getPaymentDate() != null) salary.setPaymentDate(request.getPaymentDate());
		if (request.getBaseSalary() != null) salary.setBaseSalary(request.getBaseSalary());
		if (request.getOvertimeAllowance() != null) salary.setOvertimeAllowance(request.getOvertimeAllowance());
		if (request.getNightAllowance() != null) salary.setNightAllowance(request.getNightAllowance());
		if (request.getDutyAllowance() != null) salary.setDutyAllowance(request.getDutyAllowance());
		if (request.getBonus() != null) salary.setBonus(request.getBonus());
		if (request.getIncomeTax() != null) salary.setIncomeTax(request.getIncomeTax());
		if (request.getNationalPension() != null) salary.setNationalPension(request.getNationalPension());
		if (request.getHealthInsurance() != null) salary.setHealthInsurance(request.getHealthInsurance());
		if (request.getEmploymentInsurance() != null) salary.setEmploymentInsurance(request.getEmploymentInsurance());
		if (request.getSocietyFee() != null) salary.setSocietyFee(request.getSocietyFee());
		if (request.getAdvancePayment() != null) salary.setAdvancePayment(request.getAdvancePayment());
		if (request.getOtherDeductions() != null) salary.setOtherDeductions(request.getOtherDeductions());
		if (request.getSalaryStatus() != null) salary.setSalaryStatus(request.getSalaryStatus());
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
}