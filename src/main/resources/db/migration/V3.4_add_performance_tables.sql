CREATE TABLE performance_evaluations (
    evaluation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 1. 누구를 평가했는가 (직원)
    employeeId BIGINT NOT NULL, 
    
    -- 2. 언제 평가했는가 (시기)
    evaluation_year YEAR NOT NULL,
    evaluation_quarter INT NOT NULL,  -- (1, 2, 3, 4분기)
    
    -- 3. 어떻게 평가했는가 (점수)
    attitude_score TINYINT,       -- 근무태도 점수 (1-5점)
    achievement_score TINYINT,    -- 목표달성 점수 (1-5점)
    collaboration_score TINYINT,  -- 협업 점수 (1-5점)
    
    -- 4. 평가 결과 (등급)
    contribution_grade VARCHAR(2), -- 기여도 등급 (A, B, C...)
    total_grade VARCHAR(2),        -- 'T' (최종 등급)
    
    -- 5. 평가 상태 (저장/제출)
    status ENUM('임시저장', '제출완료') NOT NULL DEFAULT '임시저장',
    
    -- 6. 누가 평가했는가 (관리자)
    evaluator_id BIGINT,          -- 평가를 입력한 관리자의 employeeId
    
    -- 7. 기타
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 외래 키 설정
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (evaluator_id) REFERENCES employees(employeeId),
    
    -- 한 직원이 한 분기에 하나의 평가만 갖도록 중복 방지
    UNIQUE KEY uk_employee_evaluation (employeeId, evaluation_year, evaluation_quarter)
);