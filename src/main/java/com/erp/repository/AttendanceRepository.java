package com.erp.repository;

import com.erp.entity.Attendance;
import com.erp.entity.Employee;
import com.erp.entity.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // 특정 직원의 모든 근태 조회
    List<Attendance> findByEmployeeOrderByCheckInDesc(Employee employee);
    
    // 특정 직원의 기간별 근태 조회
    List<Attendance> findByEmployeeAndCheckInBetweenOrderByCheckInDesc(
        Employee employee, LocalDateTime startDate, LocalDateTime endDate
    );
    
    // 특정 직원의 최근 근태 조회
    Optional<Attendance> findFirstByEmployeeOrderByCheckInDesc(Employee employee);
    
    // 특정 직원의 오늘 근태 조회
    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND DATE(a.checkIn) = CURRENT_DATE")
    Optional<Attendance> findTodayAttendance(@Param("employee") Employee employee);
    
    // 근태 타입별 조회
    List<Attendance> findByAttendanceTypeAndCheckInBetweenOrderByCheckInDesc(
        AttendanceType type, LocalDateTime startDate, LocalDateTime endDate
    );
    
    // 전체 직원 기간별 근태 조회 (관리자용)
    List<Attendance> findByCheckInBetweenOrderByCheckInDesc(
        LocalDateTime startDate, LocalDateTime endDate
    );
    
    // 특정 직원의 초과근무 시간 합계
    @Query("SELECT COALESCE(SUM(a.overtimeHours), 0.0) FROM Attendance a " +
           "WHERE a.employee = :employee AND a.checkIn BETWEEN :startDate AND :endDate")
    Double sumOvertimeHoursByEmployeeAndPeriod(
        @Param("employee") Employee employee,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
