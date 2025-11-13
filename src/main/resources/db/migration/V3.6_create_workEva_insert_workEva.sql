CREATE TABLE WorkEvaluations (
    -- 기본 키
    evaluationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 직원 ID (FK)
    employeeId BIGINT NOT NULL,
    
    -- 평가 연도 및 분기
    evaluation_year INT NOT NULL,
    evaluation_quarter INT NOT NULL,
    
    -- 평가 점수
    attitude_score INT,
    achievement_score INT,
    collaboration_score INT,
    
    -- 평가 등급
    contribution_grade VARCHAR(2),
    total_grade VARCHAR(2),
    
    -- 상태 (Enum: 임시저장, 제출완료)
    status VARCHAR(50) NOT NULL DEFAULT '임시저장',
    
    -- 평가자 ID (FK)
    evaluatorId BIGINT,
    
    -- 타임스탬프
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 유니크 제약 조건 (직원별 연도/분기 1회)
    CONSTRAINT uk_employee_evaluation UNIQUE (employeeId, evaluation_year, evaluation_quarter)
);
ALTER TABLE WorkEvaluations 
    ADD CONSTRAINT fk_evaluation_employee 
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId);

ALTER TABLE WorkEvaluations 
    ADD CONSTRAINT fk_evaluation_evaluator 
    FOREIGN KEY (evaluatorId) REFERENCES employees(employeeId);