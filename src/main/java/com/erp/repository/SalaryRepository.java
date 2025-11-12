
package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
	List<Salary> findByEmployee(Employee employee);
	Optional<Salary> findByEmployeeAndPaymentDate(Employee employee, YearMonth paymentDate);
	List<Salary> findByPaymentDateAndSalaryStatus(YearMonth paymentDate, SalaryStatus salaryStatus);
}