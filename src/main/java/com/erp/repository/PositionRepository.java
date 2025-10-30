package com.erp.repository;

import com.erp.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {
    Optional<Position> findByName(String name);
    List<Position> findByRankLevelGreaterThanEqual(Integer rankLevel);
    List<Position> findAllByOrderByRankLevelDesc();
}