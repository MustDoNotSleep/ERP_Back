
package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.Salary;
import com.erp.entity.enums.SalaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
	List<Salary> findByEmployee(Employee employee);
	Optional<Salary> findByEmployeeAndPaymentDate(Employee employee, YearMonth paymentDate);
	List<Salary> findByPaymentDate(YearMonth paymentDate);
	List<Salary> findByPaymentDateAndSalaryStatus(YearMonth paymentDate, SalaryStatus salaryStatus);
	
	// 특정 직원의 기간별 급여 조회 (퇴직금 계산용)
	@Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId " +
		   "AND s.paymentDate BETWEEN :startMonth AND :endMonth " +
		   "ORDER BY s.paymentDate DESC")
	List<Salary> findByEmployeeIdAndPaymentDateBetween(
		@Param("employeeId") Long employeeId,
		@Param("startMonth") YearMonth startMonth,
		@Param("endMonth") YearMonth endMonth
	);
	
	@Query("SELECT s FROM Salary s " +
		   "LEFT JOIN FETCH s.employee e " +
		   "LEFT JOIN FETCH e.department " +
		   "LEFT JOIN FETCH e.position " +
		   "WHERE s.paymentDate = :paymentDate")
	List<Salary> findByPaymentDateWithEmployee(@Param("paymentDate") YearMonth paymentDate);
	
	// 부서별 필터링
	@Query("SELECT s FROM Salary s " +
		   "LEFT JOIN FETCH s.employee e " +
		   "LEFT JOIN FETCH e.department d " +
		   "LEFT JOIN FETCH e.position " +
		   "WHERE s.paymentDate = :paymentDate " +
		   "AND d.departmentName = :departmentName")
	List<Salary> findByPaymentDateAndDepartment(
		@Param("paymentDate") YearMonth paymentDate, 
		@Param("departmentName") String departmentName
	);
	
	// 직급별 필터링
	@Query("SELECT s FROM Salary s " +
		   "LEFT JOIN FETCH s.employee e " +
		   "LEFT JOIN FETCH e.department " +
		   "LEFT JOIN FETCH e.position p " +
		   "WHERE s.paymentDate = :paymentDate " +
		   "AND p.positionName = :positionName")
	List<Salary> findByPaymentDateAndPosition(
		@Param("paymentDate") YearMonth paymentDate, 
		@Param("positionName") String positionName
	);
}