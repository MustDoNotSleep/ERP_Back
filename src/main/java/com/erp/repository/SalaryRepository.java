// package com.erp.repository;

// import com.erp.entity.Salary;
// import com.erp.entity.Employee;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// import java.math.BigDecimal;
// import java.time.YearMonth;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface SalaryRepository extends JpaRepository<Salary, String> {
//     List<Salary> findByEmployee(Employee employee);
//     Optional<Salary> findByEmployeeAndPaymentDate(Employee employee, YearMonth paymentDate);
//     List<Salary> findByPaymentDateAndStatus(YearMonth paymentDate, Salary.SalaryStatus status);
    
//     @Query("SELECT AVG(s.totalSalary) FROM Salary s WHERE s.employee.department.id = ?1 AND s.status = 'PAID'")
//     BigDecimal findAverageSalaryByDepartment(String departmentId);
    
//     @Query("SELECT s FROM Salary s WHERE s.paymentDate = ?1 AND s.status = 'DRAFT'")
//     List<Salary> findUnconfirmedSalariesForMonth(YearMonth paymentDate);
// }