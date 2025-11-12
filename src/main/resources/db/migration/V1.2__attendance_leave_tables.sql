-- V1.2: Attendance, Leave 테이블 생성

-- 1. leaves 테이블
CREATE TABLE leaves (
    leaveId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    type VARCHAR(50) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    leaveDays DOUBLE NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    annualLeaveBalanceId BIGINT,
    approvedBy BIGINT,
    approvedAt DATE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    FOREIGN KEY (approvedBy) REFERENCES employees(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. attendance 테이블
CREATE TABLE attendance (
    attendanceId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    checkIn TIMESTAMP NOT NULL,
    checkOut TIMESTAMP,
    attendanceType VARCHAR(50) NOT NULL,
    note VARCHAR(500),
    leaveId BIGINT,
    workHours DOUBLE,
    overtimeHours DOUBLE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    FOREIGN KEY (leaveId) REFERENCES leaves(leaveId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_leave_employee ON leaves(employeeId);
CREATE INDEX idx_leave_status ON leaves(status);
CREATE INDEX idx_leave_dates ON leaves(startDate, endDate);
CREATE INDEX idx_attendance_employee ON attendance(employeeId);
CREATE INDEX idx_attendance_checkin ON attendance(checkIn);