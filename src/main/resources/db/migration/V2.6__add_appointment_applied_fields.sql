-- 발령 적용 여부 추적을 위한 필드 추가
ALTER TABLE AppointmentRequests
ADD COLUMN isApplied BOOLEAN NOT NULL DEFAULT FALSE COMMENT '발령 적용 여부 (스케줄러가 실제로 적용했는지)',
ADD COLUMN appliedDate DATETIME COMMENT '발령 실제 적용 일시 (스케줄러가 적용한 시점)';

-- Safe Update Mode 해제
SET SQL_SAFE_UPDATES = 0;

-- 기존 승인된 발령 중 발령일자가 과거인 건들은 이미 수동으로 적용되었다고 가정
UPDATE AppointmentRequests
SET isApplied = TRUE,
    appliedDate = processedDate
WHERE status = 'APPROVED'
  AND effectiveDate < CURDATE();

-- Safe Update Mode 복원
SET SQL_SAFE_UPDATES = 1;
