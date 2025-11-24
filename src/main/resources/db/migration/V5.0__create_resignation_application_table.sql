-- V5.0__create_resignation_application_table.sql
-- 퇴직 신청 테이블 생성

CREATE TABLE resignation_applications (
    resignationId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '퇴직 신청 ID',
    employeeId BIGINT NOT NULL COMMENT '신청자 직원 ID',
    desiredResignationDate DATE NOT NULL COMMENT '퇴직 희망일',
    reason TEXT NOT NULL COMMENT '퇴직 사유',
    detailedReason TEXT COMMENT '상세 사유',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '신청 상태 (PENDING, APPROVED, REJECTED)',
    applicationDate DATETIME NOT NULL COMMENT '신청 일시',
    processedBy BIGINT COMMENT '처리자 직원 ID',
    processedAt DATETIME COMMENT '처리 일시',
    rejectionReason VARCHAR(500) COMMENT '반려 사유',
    finalResignationDate DATE COMMENT '최종 퇴사일 (승인 시)',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    
    -- 외래 키 제약조건
    CONSTRAINT fk_resignation_employee FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    CONSTRAINT fk_resignation_processor FOREIGN KEY (processedBy) REFERENCES employees(employeeId) ON DELETE SET NULL,
    
    -- 체크 제약조건
    CONSTRAINT chk_resignation_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='퇴직 신청';

-- 인덱스 생성
CREATE INDEX idx_resignation_employee ON resignation_applications(employeeId);
CREATE INDEX idx_resignation_status ON resignation_applications(status);
CREATE INDEX idx_resignation_application_date ON resignation_applications(applicationDate);
CREATE INDEX idx_resignation_desired_date ON resignation_applications(desiredResignationDate);
CREATE INDEX idx_resignation_processor ON resignation_applications(processedBy);
