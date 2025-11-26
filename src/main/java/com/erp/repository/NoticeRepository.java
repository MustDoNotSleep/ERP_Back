package com.erp.repository;

import com.erp.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    // 활성화된 공지사항 전체 조회 (페이징)
    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.author a " +
           "LEFT JOIN FETCH a.department d " +
           "WHERE n.isActive = true " +
           "ORDER BY n.isImportant DESC, n.createdAt DESC")
    Page<Notice> findAllActive(Pageable pageable);
    
    // ID로 활성화된 공지사항 조회
    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.author a " +
           "LEFT JOIN FETCH a.department d " +
           "WHERE n.id = :id AND n.isActive = true")
    Optional<Notice> findActiveById(@Param("id") Long id);
    
    // 중요 공지사항만 조회
    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.author a " +
           "WHERE n.isActive = true AND n.isImportant = true " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findImportantNotices();
    
    // 제목 검색 (페이징)
    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.author a " +
           "LEFT JOIN FETCH a.department d " +
           "WHERE n.isActive = true AND n.title LIKE %:keyword% " +
           "ORDER BY n.isImportant DESC, n.createdAt DESC")
    Page<Notice> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}
