// package com.erp.service;

// import com.erp.dto.SalaryDto;
// import com.erp.entity.Employee;
// import com.erp.entity.Salary;
// import com.erp.exception.BusinessException;
// import com.erp.exception.EntityNotFoundException;
// import com.erp.repository.EmployeeRepository;
// import com.erp.repository.SalaryRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.YearMonth;
// import java.util.List;
// import java.util.IDENTITY;
// import java.util.stream.Collectors;

// @Service
// @Transactional(readOnly = true)
// @RequiredArgsConstructor
// public class SalaryService {
    
//     private final SalaryRepository salaryRepository;
//     private final EmployeeRepository employeeRepository;
    
//     private static final BigDecimal INCOME_TAX_RATE = new BigDecimal("0.1");
//     private static final BigDecimal NATIONAL_PENSION_RATE = new BigDecimal("0.045");
//     private static final BigDecimal HEALTH_INSURANCE_RATE = new BigDecimal("0.0343");
//     private static final BigDecimal EMPLOYMENT_INSURANCE_RATE = new BigDecimal("0.008");
    
//     @Transactional
//     public String createSalary(SalaryDto.Request request) {
//         Employee employee = findEmployee(request.getEmployeeId());
//         validateNoDuplicateSalary(employee, request.getPaymentDate());
        
//         Salary salary = Salary.builder()
//             .id(IDENTITY.randomIDENTITY().toString())
//             .employee(employee)
//             .paymentDate(request.getPaymentDate())
//             .baseSalary(request.getBaseSalary())
//             .overtime(request.getOvertime())
//             .bonus(request.getBonus())
//             .mealAllowance(request.getMealAllowance())
//             .transportAllowance(request.getTransportAllowance())
//             .status(Salary.SalaryStatus.DRAFT)
//             .build();
        
//         calculateDeductions(salary);
//         salary.calculateTotal();
//         salary.calculateNetSalary();
        
//         salaryRepository.save(salary);
//         return salary.getId();
//     }
    
//     public SalaryDto.Response getSalary(String id) {
//         return salaryRepository.findById(id)
//             .map(SalaryDto.Response::from)
//             .orElseThrow(() -> new EntityNotFoundException("Salary", id));
//     }
    
//     public List<SalaryDto.Response> getEmployeeSalaries(String employeeId) {
//         Employee employee = findEmployee(employeeId);
//         return salaryRepository.findByEmployee(employee).stream()
//             .map(SalaryDto.Response::from)
//             .collect(Collectors.toList());
//     }
    
//     public List<SalaryDto.Response> getMonthlySalaries(YearMonth paymentDate) {
//         return salaryRepository.findByPaymentDateAndStatus(paymentDate, Salary.SalaryStatus.PAID)
//             .stream()
//             .map(SalaryDto.Response::from)
//             .collect(Collectors.toList());
//     }
    
//     @Transactional
//     public void confirmSalary(String id) {
//         Salary salary = findSalary(id);
//         if (salary.getStatus() != Salary.SalaryStatus.DRAFT) {
//             throw new BusinessException("Salary is not in DRAFT status", "INVALID_STATUS");
//         }
//         salary.confirm();
//     }
    
//     @Transactional
//     public void markAsPaid(String id) {
//         Salary salary = findSalary(id);
//         if (salary.getStatus() != Salary.SalaryStatus.CONFIRMED) {
//             throw new BusinessException("Salary is not in CONFIRMED status", "INVALID_STATUS");
//         }
//         salary.markAsPaid();
//     }
    
//     private void calculateDeductions(Salary salary) {
//         BigDecimal totalIncome = salary.getBaseSalary()
//             .add(salary.getOvertime() != null ? salary.getOvertime() : BigDecimal.ZERO)
//             .add(salary.getBonus() != null ? salary.getBonus() : BigDecimal.ZERO);
        
//         salary.setIncomeTax(totalIncome.multiply(INCOME_TAX_RATE)
//             .setScale(0, RoundingMode.HALF_UP));
//         salary.setNationalPension(totalIncome.multiply(NATIONAL_PENSION_RATE)
//             .setScale(0, RoundingMode.HALF_UP));
//         salary.setHealthInsurance(totalIncome.multiply(HEALTH_INSURANCE_RATE)
//             .setScale(0, RoundingMode.HALF_UP));
//         salary.setEmploymentInsurance(totalIncome.multiply(EMPLOYMENT_INSURANCE_RATE)
//             .setScale(0, RoundingMode.HALF_UP));
//     }
    
//     private Employee findEmployee(String id) {
//         return employeeRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Employee", id));
//     }
    
//     private Salary findSalary(String id) {
//         return salaryRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Salary", id));
//     }
    
//     private void validateNoDuplicateSalary(Employee employee, YearMonth paymentDate) {
//         if (salaryRepository.findByEmployeeAndPaymentDate(employee, paymentDate).isPresent()) {
//             throw new BusinessException(
//                 "Salary already exists for this month",
//                 "DUPLICATE_SALARY"
//             );
//         }
//     }
// }