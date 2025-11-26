-- 공지사항 테이블 생성
CREATE TABLE notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '공지사항 제목',
    content TEXT NOT NULL COMMENT '공지사항 내용',
    authorId BIGINT NOT NULL COMMENT '작성자 ID',
    isImportant BOOLEAN NOT NULL DEFAULT FALSE COMMENT '중요 공지 여부',
    isActive BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    viewCount INT NOT NULL DEFAULT 0 COMMENT '조회수',
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    CONSTRAINT fk_notice_author FOREIGN KEY (authorId) 
        REFERENCES employees(employeeId) ON DELETE CASCADE,
    
    INDEX idx_notice_important (isImportant),
    INDEX idx_notice_created_at (createdAt),
    INDEX idx_notice_active (isActive)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공지사항';
