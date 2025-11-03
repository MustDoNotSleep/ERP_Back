-- V1.2__attendance_leave_tables.sql

-- 1. Attendance Table
CREATE TABLE attendance (
    attendanceId BIGINT(36) PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    checkIn DATETIME NOT NULL,
    checkOut DATETIME,
    type ENUM('일반', '지각', '조퇴', '결근', '재택') NOT NULL,
    note VARCHAR(500),
    workHours DOUBLE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 2. Leave Requests Table
CREATE TABLE leaves (
    leaveId VARCHAR(36) PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    type ENUM('연차', '병가', '개인', '출산', '배우자출산', '조의') NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    reason TEXT,
    status ENUM('대기', '승인', '반려', '취소') NOT NULL DEFAULT '대기',
    approvedBy BIGINT,
    approvedAt DATE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (approvedBy) REFERENCES employees(employeeId)
);

-- 3. Indexes
CREATE INDEX idx_attendance_employee ON attendance(employeeId);
CREATE INDEX idx_attendance_checkin ON attendance(checkIn);
CREATE INDEX idx_leave_employee ON leaves(employeeId);
CREATE INDEX idx_leave_status ON leaves(status);