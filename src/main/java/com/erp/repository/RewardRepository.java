package com.erp.repository;

import com.erp.entity.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RewardRepository extends JpaRepository<Rewards, Long> {
    
    @Query("SELECT r FROM Rewards r " +
           "JOIN r.employee e " +
           "LEFT JOIN e.department d " +
           "WHERE (:startDate IS NULL OR r.rewardDate >= :startDate) " +
           "AND (:endDate IS NULL OR r.rewardDate <= :endDate) " +
           "AND (:empName IS NULL OR e.name LIKE %:empName%) " +
           "AND (:deptName IS NULL OR d.teamName LIKE %:deptName%) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "ORDER BY r.rewardDate DESC, r.createdAt DESC")
    List<Rewards> searchRewards(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("empName") String empName,
            @Param("deptName") String deptName,
            @Param("status") com.erp.entity.enums.RewardStatus status
    );
}
