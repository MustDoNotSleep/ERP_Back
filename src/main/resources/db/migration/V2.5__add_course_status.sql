-- V2.5: Course 테이블에 승인 상태 관련 컬럼 추가

ALTER TABLE Courses
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING' AFTER creator,
ADD COLUMN approver BIGINT NULL AFTER status,
ADD COLUMN processedDate DATETIME NULL AFTER approver,
ADD COLUMN comment VARCHAR(500) NULL AFTER processedDate,
ADD CONSTRAINT fk_course_approver FOREIGN KEY (approver) REFERENCES employees(employeeId) ON DELETE SET NULL;

-- 기존 데이터는 모두 PENDING 상태로 설정됨 (DEFAULT 값)
