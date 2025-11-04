-- attendance 테이블 삭제 후 재생성 (데이터 손실 주의!)

-- 1. 기존 테이블 삭제
DROP TABLE IF EXISTS attendance;

-- 2. 새로운 구조로 재생성
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

-- 3. 인덱스 생성
CREATE INDEX idx_attendance_employee ON attendance(employeeId);
CREATE INDEX idx_attendance_checkin ON attendance(checkIn);
