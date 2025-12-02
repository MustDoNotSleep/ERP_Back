-- ============================================
-- 1. EvaluationPolicy (Master Table)
-- ============================================

CREATE TABLE EvaluationPolicy (
    policyId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '정책 ID',
    
    -- 평가 시즌 정보
    seasonName VARCHAR(100) NOT NULL COMMENT '시즌명 (예: 2025년 1분기)',
    startDate DATE NOT NULL COMMENT '평가 시작일',
    endDate DATE NOT NULL COMMENT '평가 종료일',
    
    -- 평가 설정
    evaluationType VARCHAR(20) NOT NULL COMMENT '평가유형 (KPI/LEADERSHIP)',
    evaluationSection VARCHAR(20) DEFAULT '부서별' COMMENT '평가부문 (고정값)',
    
    -- 파일 정보
    evaluationFormPath VARCHAR(255) COMMENT '업로드한 양식 파일 경로',
    originalFileName VARCHAR(255) COMMENT '원본 파일명',
    
    -- 가중치
    performanceWeight INT DEFAULT 70 COMMENT '성과평가 비중(%)',
    competencyWeight INT DEFAULT 30 COMMENT '역량평가 비중(%)',
    
    -- ⭐ 타겟팅 및 매핑 (BIGINT 정규화 FK 구조)
    targetDepartmentId BIGINT COMMENT '대상 부서 ID',
    targetPositionId BIGINT COMMENT '대상 직급 ID',
    mappingMethod VARCHAR(50) DEFAULT '자동지정' COMMENT '매핑 방식 (고정값)',
    
    -- 관리자 정보
    createdById BIGINT NOT NULL COMMENT '생성자 사번',
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    -- FK 설정
    FOREIGN KEY (createdById) REFERENCES employees(employeeId),
    FOREIGN KEY (targetDepartmentId) REFERENCES departments(departmentId),
    FOREIGN KEY (targetPositionId) REFERENCES positions(positionId)
);


-- ============================================
-- 2. EvaluationPolicyDetail (Detail Table)
-- ============================================

CREATE TABLE EvaluationPolicyDetail (
    detailId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상세 ID',
    
    -- 정책 FK
    policyId BIGINT NOT NULL COMMENT 'EvaluationPolicy ID',
    
    -- 직원 정보 (FK + Snapshot)
    employeeId BIGINT NOT NULL COMMENT '직원 사번',
    employeeName VARCHAR(50) COMMENT '이름 스냅샷',
    teamName VARCHAR(50) COMMENT '부서명 스냅샷',
    
    -- 결과값
    finalScore DOUBLE DEFAULT 0.0 COMMENT '최종 점수',
    finalGrade VARCHAR(10) COMMENT '최종 등급',
    
    -- FK 설정
    FOREIGN KEY (policyId) REFERENCES EvaluationPolicy(policyId) ON DELETE CASCADE,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);
