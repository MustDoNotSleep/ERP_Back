package com.erp.dto;

import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

public class SalaryDto {
	@Getter
	@Builder
	public static class Request {
		private Long employeeId;
		private YearMonth paymentDate;
		private BigDecimal baseSalary;
		private BigDecimal overtimeAllowance;
		private BigDecimal nightAllowance;
		private BigDecimal dutyAllowance;
		private BigDecimal bonus;
		private BigDecimal incomeTax;
		private BigDecimal nationalPension;
		private BigDecimal healthInsurance;
		private BigDecimal employmentInsurance;
		private BigDecimal societyFee;
		private BigDecimal advancePayment;
		private BigDecimal otherDeductions;
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
		private YearMonth paymentDate;
		private BigDecimal baseSalary;
		private BigDecimal overtimeAllowance;
		private BigDecimal nightAllowance;
		private BigDecimal dutyAllowance;
		private BigDecimal bonus;
		private BigDecimal incomeTax;
		private BigDecimal nationalPension;
		private BigDecimal healthInsurance;
		private BigDecimal employmentInsurance;
		private BigDecimal societyFee;
		private BigDecimal advancePayment;
		private BigDecimal otherDeductions;
		private BigDecimal totalSalary;
		private BigDecimal netSalary;
		private SalaryStatus salaryStatus;

		public static Response from(Salary salary) {
			return Response.builder()
				.id(salary.getId())
				.employeeId(salary.getEmployee().getId())
				.employeeName(salary.getEmployee().getName())
				.departmentName(salary.getEmployee().getDepartment() != null ? salary.getEmployee().getDepartment().getDepartmentName() : null)
				.positionName(salary.getEmployee().getPosition() != null ? salary.getEmployee().getPosition().getPositionName() : null)
				.paymentDate(salary.getPaymentDate())
				.baseSalary(salary.getBaseSalary())
				.overtimeAllowance(salary.getOvertimeAllowance())
				.nightAllowance(salary.getNightAllowance())
				.dutyAllowance(salary.getDutyAllowance())
				.bonus(salary.getBonus())
				.incomeTax(salary.getIncomeTax())
				.nationalPension(salary.getNationalPension())
				.healthInsurance(salary.getHealthInsurance())
				.employmentInsurance(salary.getEmploymentInsurance())
				.societyFee(salary.getSocietyFee())
				.advancePayment(salary.getAdvancePayment())
				.otherDeductions(salary.getOtherDeductions())
				.totalSalary(salary.getTotalSalary())
				.netSalary(salary.getNetSalary())
				.salaryStatus(salary.getSalaryStatus())
				.build();
		}
	}
}