-- 연차 관리 시스템 개선

-- 1. annual_leave_balance 테이블 생성
CREATE TABLE IF NOT EXISTS annual_leave_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    year INT NOT NULL,
    totalDays DOUBLE NOT NULL DEFAULT 0 COMMENT '총 연차 일수',
    usedDays DOUBLE NOT NULL DEFAULT 0 COMMENT '사용한 연차 일수',
    remainingDays DOUBLE NOT NULL DEFAULT 0 COMMENT '남은 연차 일수',
    expiryDate DATE COMMENT '연차 만료일',
    note VARCHAR(500) COMMENT '비고',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    CONSTRAINT fk_annual_leave_employee FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    UNIQUE KEY uk_employee_year (employeeId, year)
) COMMENT='연차 잔여 관리';

-- 2. leaves 테이블에 leaveDays 및 annualLeaveBalanceId 컬럼 추가
ALTER TABLE leaves ADD COLUMN leaveDays DOUBLE NOT NULL DEFAULT 1.0 COMMENT '실제 사용 일수' AFTER duration;
ALTER TABLE leaves ADD COLUMN annualLeaveBalanceId BIGINT NULL COMMENT '사용한 연차 잔여 ID (연차인 경우만)' AFTER leaveDays;

-- 2-1. attendance 테이블에 leaveId 컬럼 추가
ALTER TABLE attendance ADD COLUMN leaveId BIGINT NULL COMMENT '휴가 참조 (휴가인 경우만)' AFTER note;

-- 3. 외래키 및 인덱스 생성
ALTER TABLE leaves ADD CONSTRAINT fk_leave_annual_balance FOREIGN KEY (annualLeaveBalanceId) REFERENCES annual_leave_balance(id) ON DELETE SET NULL;
ALTER TABLE attendance ADD CONSTRAINT fk_attendance_leave FOREIGN KEY (leaveId) REFERENCES leaves(leaveId) ON DELETE SET NULL;

CREATE INDEX idx_leave_annual_balance ON leaves(annualLeaveBalanceId);
CREATE INDEX idx_attendance_leave ON attendance(leaveId);
CREATE INDEX idx_annual_leave_year ON annual_leave_balance(year);
CREATE INDEX idx_annual_leave_employee_year ON annual_leave_balance(employeeId, year);
