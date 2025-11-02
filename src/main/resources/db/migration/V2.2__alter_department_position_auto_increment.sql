-- V2.2__alter_department_position_auto_increment.sql
-- Department와 Position 테이블의 ID를 AUTO_INCREMENT로 변경

-- 1. Department의 departmentId를 AUTO_INCREMENT로 변경
ALTER TABLE departments MODIFY COLUMN departmentId BIGINT NOT NULL AUTO_INCREMENT;

-- 2. Position의 positionId를 AUTO_INCREMENT로 변경
ALTER TABLE positions MODIFY COLUMN positionId BIGINT NOT NULL AUTO_INCREMENT;
