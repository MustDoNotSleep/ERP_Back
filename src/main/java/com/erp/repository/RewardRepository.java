package com.erp.repository;

import com.erp.entity.Rewards;
import com.erp.entity.enums.RewardStatus; // ⭐ [필수] 결재 상태 Enum 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Rewards, Long> {

    @Query("SELECT r FROM Rewards r " +
            "JOIN FETCH r.employee e " +        // 신청자 정보 (필수)
            "LEFT JOIN FETCH e.department d " +
            "LEFT JOIN FETCH e.position p " + // 부서 정보 (선택)
            "LEFT JOIN FETCH r.approver a " +   // 승인자 정보 (선택 - 이름 표시용)
            
            "WHERE (:startDate IS NULL OR r.rewardDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.rewardDate <= :endDate) " +
            "AND (:employeeName IS NULL OR e.name LIKE CONCAT ('%', :employeeName, '%')) " +
            "AND (:deptName IS NULL OR d.teamName LIKE CONCAT ('%', :deptName, '%')) " +
            "AND (:positionName IS NULL OR p.positionName LIKE CONCAT ('%', :positionName, '%')) " +
            "AND (:status IS NULL OR r.status = :status) " + // 여기서 r.status는 RewardStatus 타입임
            "ORDER BY r.rewardDate DESC, r.createdAt DESC")
    List<Rewards> searchRewards(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("employeeName") String employeeName,
            @Param("deptName") String deptName,
            @Param("positionName") String positionName,
            @Param("status") RewardStatus status 
    );
}