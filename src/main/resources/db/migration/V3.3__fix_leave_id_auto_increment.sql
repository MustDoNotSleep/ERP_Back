-- leaves 테이블의 leaveId AUTO_INCREMENT 수정

-- 1. attendance 테이블의 외래 키 제거 (leaveId 참조)
ALTER TABLE attendance DROP FOREIGN KEY fk_attendance_leave;

-- 2. leaves 테이블의 외래 키들 제거
ALTER TABLE leaves DROP FOREIGN KEY leaves_ibfk_1;  -- employeeId FK
ALTER TABLE leaves DROP FOREIGN KEY leaves_ibfk_2;  -- approvedBy FK

-- 3. PRIMARY KEY 제거
ALTER TABLE leaves DROP PRIMARY KEY;

-- 4. leaveId 컬럼 타입 수정 및 AUTO_INCREMENT 설정
ALTER TABLE leaves 
MODIFY COLUMN leaveId BIGINT AUTO_INCREMENT PRIMARY KEY;

-- 5. leaves 테이블의 외래 키 다시 추가
ALTER TABLE leaves 
ADD CONSTRAINT fk_leave_employee 
FOREIGN KEY (employeeId) REFERENCES employees(employeeId);

ALTER TABLE leaves 
ADD CONSTRAINT fk_leave_approved_by 
FOREIGN KEY (approvedBy) REFERENCES employees(employeeId);

-- 6. attendance 테이블의 외래 키 다시 추가
ALTER TABLE attendance 
ADD CONSTRAINT fk_attendance_leave 
FOREIGN KEY (leaveId) REFERENCES leaves(leaveId) ON DELETE SET NULL;
