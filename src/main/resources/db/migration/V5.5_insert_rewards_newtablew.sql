CREATE TABLE rewards (
    -- 1. 기본 키 (PK)
    rewardId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '포상 ID',

    -- 2. 대상자 정보 (FK)
    employeeId BIGINT NOT NULL COMMENT '포상 대상자 사번',
    
    -- 3. 포상 상세 정보
    rewardDate DATE NOT NULL COMMENT '요청일/추천일',
    rewardType VARCHAR(50) NOT NULL COMMENT '포상 종류 (ENUM)',
    rewardItem VARCHAR(50) NOT NULL COMMENT '포상 형태 (ENUM)',
    rewardValue VARCHAR(100) NULL COMMENT '포상 가치',
    reason TEXT NULL COMMENT '포상 사유',

    -- 4. 결재 상태
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL COMMENT '결재 상태',

    -- 5. 승인자 정보 (FK)
    approverId BIGINT NULL COMMENT '승인자 사번',
    approvedAt DATETIME NULL COMMENT '승인 일시',

    -- 6. 시스템 관리 컬럼
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    -- 7. 외래 키 제약조건
    -- (주의: employees 테이블의 PK가 employeeId 라고 가정했습니다)
    CONSTRAINT fk_rewards_employee FOREIGN KEY (employeeId) REFERENCES employees (employeeId),
    CONSTRAINT fk_rewards_approver FOREIGN KEY (approverId) REFERENCES employees (employeeId)
);