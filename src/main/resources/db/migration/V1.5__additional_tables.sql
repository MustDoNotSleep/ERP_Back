-- V1.5: AppointmentRequest, AnnualLeaveBalance, PerformanceEvaluation 테이블 생성

-- 1. AppointmentRequests 테이블
CREATE TABLE AppointmentRequests (
    appointmentRequestId BIGINT AUTO_INCREMENT PRIMARY KEY,
    targetEmployeeId BIGINT NOT NULL,
    requestingEmployeeId BIGINT NOT NULL,
    appointmentType VARCHAR(50) NOT NULL,
    newDepartmentId BIGINT,
    newPositionId BIGINT,
    effectiveDate DATE,
    reason TEXT,
    status VARCHAR(50) NOT NULL,
    approverId BIGINT,
    requestDate TIMESTAMP,
    processedDate TIMESTAMP,
    isApplied BOOLEAN NOT NULL DEFAULT FALSE,
    appliedDate TIMESTAMP,
    FOREIGN KEY (targetEmployeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (requestingEmployeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (newDepartmentId) REFERENCES departments(departmentId),
    FOREIGN KEY (newPositionId) REFERENCES positions(positionId),
    FOREIGN KEY (approverId) REFERENCES employees(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. annual_leave_balance 테이블
CREATE TABLE annual_leave_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    year INT NOT NULL,
    totalDays DOUBLE NOT NULL,
    usedDays DOUBLE NOT NULL,
    remainingDays DOUBLE NOT NULL,
    expiryDate DATE,
    note VARCHAR(500),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. performance_evaluations 테이블
CREATE TABLE performance_evaluations (
    evaluationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    evaluation_year INT NOT NULL,
    evaluation_quarter INT NOT NULL,
    attitude_score INT,
    achievement_score INT,
    collaboration_score INT,
    contribution_grade VARCHAR(2),
    total_grade VARCHAR(2),
    status VARCHAR(50) NOT NULL DEFAULT '임시저장',
    evaluator_id BIGINT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES employees(employeeId),
    CONSTRAINT uk_employee_evaluation UNIQUE (employeeId, evaluation_year, evaluation_quarter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 외래키 추가 (leaves -> annual_leave_balance)
ALTER TABLE leaves 
ADD CONSTRAINT fk_leaves_annual_leave_balance 
FOREIGN KEY (annualLeaveBalanceId) REFERENCES annual_leave_balance(id);

-- 인덱스 생성
CREATE INDEX idx_appointment_target ON AppointmentRequests(targetEmployeeId);
CREATE INDEX idx_appointment_status ON AppointmentRequests(status);
CREATE INDEX idx_appointment_effectiveDate ON AppointmentRequests(effectiveDate);
CREATE INDEX idx_annual_leave_employee ON annual_leave_balance(employeeId);
CREATE INDEX idx_annual_leave_year ON annual_leave_balance(year);
CREATE INDEX idx_performance_employee ON performance_evaluations(employeeId);
CREATE INDEX idx_performance_year_quarter ON performance_evaluations(evaluation_year, evaluation_quarter);