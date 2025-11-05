-- 기존 HALF_DAY, QUARTER_DAY를 AM 버전으로 변경
-- (실제 데이터는 오전/오후 정보가 없으므로 기본적으로 AM으로 설정)

-- 🔧 안전모드 해제
SET SQL_SAFE_UPDATES = 0;

ALTER TABLE leaves 
MODIFY COLUMN duration 
ENUM(
  'FULL_DAY', 
  'HALF_DAY', 'HALF_DAY_AM', 'HALF_DAY_PM', 
  'QUARTER_DAY', 'QUARTER_DAY_AM', 'QUARTER_DAY_PM'
) 
NOT NULL;


-- 기존 HALF_DAY, QUARTER_DAY를 AM 버전으로 변경
UPDATE leaves 
SET duration = 'HALF_DAY_AM' 
WHERE duration = 'HALF_DAY';

UPDATE leaves 
SET duration = 'QUARTER_DAY_AM' 
WHERE duration = 'QUARTER_DAY';

-- 잘못 계산된 leaveDays 값 수정
UPDATE leaves 
SET leaveDays = 0.5 
WHERE duration IN ('HALF_DAY_AM', 'HALF_DAY_PM');

UPDATE leaves 
SET leaveDays = 0.25 
WHERE duration IN ('QUARTER_DAY_AM', 'QUARTER_DAY_PM');

UPDATE leaves 
SET leaveDays = DATEDIFF(endDate, startDate) + 1 
WHERE duration = 'FULL_DAY' 
  AND leaveDays = 1.0 
  AND DATEDIFF(endDate, startDate) > 0;

-- 🔒 다시 안전모드 켜기 (원상복귀)
SET SQL_SAFE_UPDATES = 1;
