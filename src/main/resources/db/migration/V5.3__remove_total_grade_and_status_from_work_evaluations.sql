-- WorkEvaluations 테이블에서 total_grade와 status 컬럼 제거

ALTER TABLE WorkEvaluations DROP COLUMN total_grade;
ALTER TABLE WorkEvaluations DROP COLUMN status;
