package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId", nullable = false)
    private Employee author;
    
    @Column(name = "isImportant", nullable = false)
    private Boolean isImportant; // 중요 공지 여부
    
    @Column(name = "isActive", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 활성화 여부 (삭제 대신 비활성화)
    
    @Column(name = "viewCount", nullable = false)
    @Builder.Default
    private Integer viewCount = 0; // 조회수
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    /**
     * 비활성화 (삭제)
     */
    public void deactivate() {
        this.isActive = false;
    }
}
