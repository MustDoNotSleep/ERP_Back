package com.erp.repository;

import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByEmployee(Employee employee);
    List<Attendance> findByEmployeeAndCheckInBetween(
        Employee employee, LocalDateTime startDate, LocalDateTime endDate
    );
    
    Optional<Attendance> findFirstByEmployeeOrderByCheckInDesc(Employee employee);
    
    @Query("SELECT a FROM Attendance a WHERE a.employee = ?1 AND DATE(a.checkIn) = CURRENT_DATE")
    Optional<Attendance> findTodayAttendance(Employee employee);
    
    List<Attendance> findByTypeAndCheckInBetween(
        Attendance.AttendanceType type, LocalDateTime startDate, LocalDateTime endDate
    );
}