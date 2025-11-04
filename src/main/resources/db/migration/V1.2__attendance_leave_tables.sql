-- V1.2__attendance_leave_tables.sql

-- 1. Attendance Table
CREATE TABLE attendance (
    attendanceId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    checkIn DATETIME NOT NULL,
    checkOut DATETIME,
    attendanceType ENUM('NORMAL', 'LATE', 'EARLY_LEAVE', 'ABSENT', 'REMOTE', 'OVERTIME', 'WEEKEND_WORK', 'HOLIDAY_WORK') NOT NULL,
    note VARCHAR(500),
    workHours DOUBLE,
    overtimeHours DOUBLE DEFAULT 0.0 COMMENT '초과근무 시간',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 2. Leave Requests Table
CREATE TABLE leaves (
    leaveId BIGINT(36) BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    type ENUM('ANNUAL', 'SICK', 'MATERNITY', 'BEREAVEMENT') NOT NULL COMMENT '휴가 종류',
    duration ENUM('FULL_DAY', 'HALF_DAY', 'QUARTER_DAY') NOT NULL COMMENT '휴가 단위 (연차/반차/반반차)',
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    reason TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
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