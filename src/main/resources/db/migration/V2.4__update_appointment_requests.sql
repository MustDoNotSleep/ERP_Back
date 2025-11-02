-- AppointmentRequests 테이블 수정
-- effectiveStartDate, effectiveEndDate를 effectiveDate 하나로 변경
-- newPositionId 컬럼 추가

-- 1. newPositionId 컬럼 추가
ALTER TABLE AppointmentRequests
ADD COLUMN newPositionId BIGINT;

-- 2. 외래키 제약조건 추가
ALTER TABLE AppointmentRequests
ADD CONSTRAINT FK_AppointmentRequests_NewPosition
FOREIGN KEY (newPositionId) REFERENCES positions(positionId);

-- 3. 기존 effectiveStartDate 데이터를 effectiveDate로 복사하기 위한 임시 컬럼 추가
ALTER TABLE AppointmentRequests
ADD COLUMN effectiveDate DATE;

-- 4. 기존 데이터가 있다면 effectiveStartDate 값을 effectiveDate로 복사
UPDATE AppointmentRequests
SET effectiveDate = effectiveStartDate
WHERE effectiveStartDate IS NOT NULL;

-- 5. effectiveStartDate, effectiveEndDate 컬럼 삭제
ALTER TABLE AppointmentRequests
DROP COLUMN effectiveStartDate;

ALTER TABLE AppointmentRequests
DROP COLUMN effectiveEndDate;
