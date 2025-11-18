package com.erp.repository;

import com.erp.entity.Welfare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface WelfareRepository extends JpaRepository<Welfare, Long> {
    
    // 직원별 복리후생 내역 조회
    List<Welfare> findByEmployeeIdOrderByPaymentMonthDesc(Long employeeId);
    
    // 특정 월의 직원 복리후생 내역
    List<Welfare> findByEmployeeIdAndPaymentMonth(Long employeeId, YearMonth paymentMonth);
    
    // 특정 연도의 직원 복리후생 사용 총액
    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Welfare w " +
           "WHERE w.employee.id = :employeeId " +
           "AND w.isApproved = true " +
           "AND SUBSTRING(w.paymentMonth, 1, 4) = :year")
    BigDecimal getTotalUsedAmountByEmployeeAndYear(@Param("employeeId") Long employeeId, 
                                                     @Param("year") String year);
    
    // 승인 대기 중인 복리후생 목록
    List<Welfare> findByIsApprovedFalseOrderByCreatedAtDesc();
}
