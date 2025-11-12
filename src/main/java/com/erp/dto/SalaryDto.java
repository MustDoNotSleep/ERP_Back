package com.erp.dto;

import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
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
		private String teamName;
		
		@JsonFormat(pattern = "yyyy-MM")
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