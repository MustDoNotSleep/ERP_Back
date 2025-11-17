-- 2024년 및 2025년 출근 데이터 추가 (연차 발생을 위한 출근율 확보)

-- ============================================
-- 2024년 출근 데이터 (1년 이상 근무자)
-- ============================================

-- 2024년 평일 날짜 임시 테이블
CREATE TEMPORARY TABLE IF NOT EXISTS temp_dates_2024 (month INT, day INT);

INSERT INTO temp_dates_2024 (month, day) VALUES
-- 1월 (22일)
(1,2),(1,3),(1,4),(1,5),(1,8),(1,9),(1,10),(1,11),(1,12),(1,15),(1,16),(1,17),(1,18),(1,19),(1,22),(1,23),(1,24),(1,25),(1,26),(1,29),(1,30),(1,31),
-- 2월 (19일)
(2,1),(2,2),(2,5),(2,6),(2,7),(2,8),(2,13),(2,14),(2,15),(2,16),(2,19),(2,20),(2,21),(2,22),(2,23),(2,26),(2,27),(2,28),(2,29),
-- 3월 (20일)
(3,4),(3,5),(3,6),(3,7),(3,8),(3,11),(3,12),(3,13),(3,14),(3,15),(3,18),(3,19),(3,20),(3,21),(3,22),(3,25),(3,26),(3,27),(3,28),(3,29),
-- 4월 (21일)
(4,1),(4,2),(4,3),(4,4),(4,5),(4,8),(4,9),(4,11),(4,12),(4,15),(4,16),(4,17),(4,18),(4,19),(4,22),(4,23),(4,24),(4,25),(4,26),(4,29),(4,30),
-- 5월 (20일)
(5,2),(5,3),(5,7),(5,8),(5,9),(5,10),(5,13),(5,14),(5,16),(5,17),(5,20),(5,21),(5,22),(5,23),(5,24),(5,27),(5,28),(5,29),(5,30),(5,31),
-- 6월 (19일)
(6,3),(6,4),(6,5),(6,7),(6,10),(6,11),(6,12),(6,13),(6,14),(6,17),(6,18),(6,19),(6,20),(6,21),(6,24),(6,25),(6,26),(6,27),(6,28),
-- 7월 (22일)
(7,1),(7,2),(7,3),(7,4),(7,5),(7,8),(7,9),(7,10),(7,11),(7,12),(7,16),(7,17),(7,18),(7,19),(7,22),(7,23),(7,24),(7,25),(7,26),(7,29),(7,30),(7,31),
-- 8월 (20일)
(8,1),(8,2),(8,5),(8,6),(8,7),(8,8),(8,9),(8,12),(8,13),(8,14),(8,16),(8,19),(8,20),(8,21),(8,22),(8,23),(8,26),(8,27),(8,28),(8,29),(8,30),
-- 9월 (18일)
(9,2),(9,3),(9,4),(9,5),(9,6),(9,9),(9,10),(9,11),(9,12),(9,13),(9,19),(9,20),(9,23),(9,24),(9,25),(9,26),(9,27),(9,30),
-- 10월 (21일)
(10,1),(10,2),(10,4),(10,7),(10,8),(10,10),(10,11),(10,14),(10,15),(10,16),(10,17),(10,18),(10,21),(10,22),(10,23),(10,24),(10,25),(10,28),(10,29),(10,30),(10,31),
-- 11월 (20일)
(11,1),(11,4),(11,5),(11,6),(11,7),(11,8),(11,11),(11,12),(11,13),(11,14),(11,15),(11,18),(11,19),(11,20),(11,21),(11,22),(11,25),(11,26),(11,27),(11,28),(11,29),
-- 12월 (20일)
(12,2),(12,3),(12,4),(12,5),(12,6),(12,9),(12,10),(12,11),(12,12),(12,13),(12,16),(12,17),(12,18),(12,19),(12,20),(12,23),(12,24),(12,26),(12,27),(12,30),(12,31);

-- 1년 이상 근무자들의 2024년 출근 데이터
INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12341, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12343, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12345, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12347, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12348, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 123411, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 123412, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 1234123, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 25100807, CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2024-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2024;

DROP TEMPORARY TABLE IF EXISTS temp_dates_2024;

-- ============================================
-- 2025년 출근 데이터 (1~11월)
-- ============================================

CREATE TEMPORARY TABLE IF NOT EXISTS temp_dates_2025 (month INT, day INT);

INSERT INTO temp_dates_2025 (month, day) VALUES
-- 1월 (22일)
(1,2),(1,3),(1,6),(1,7),(1,8),(1,9),(1,10),(1,13),(1,14),(1,15),(1,16),(1,17),(1,20),(1,21),(1,22),(1,23),(1,24),(1,27),(1,28),(1,29),(1,30),(1,31),
-- 2월 (19일)
(2,3),(2,4),(2,5),(2,6),(2,7),(2,10),(2,11),(2,12),(2,13),(2,14),(2,17),(2,18),(2,19),(2,20),(2,21),(2,24),(2,25),(2,26),(2,27),(2,28),
-- 3월 (21일)
(3,3),(3,4),(3,5),(3,6),(3,7),(3,10),(3,11),(3,12),(3,13),(3,14),(3,17),(3,18),(3,19),(3,20),(3,21),(3,24),(3,25),(3,26),(3,27),(3,28),(3,31),
-- 4월 (22일)
(4,1),(4,2),(4,3),(4,4),(4,7),(4,8),(4,9),(4,10),(4,11),(4,14),(4,15),(4,16),(4,17),(4,18),(4,21),(4,22),(4,23),(4,24),(4,25),(4,28),(4,29),(4,30),
-- 5월 (19일)
(5,2),(5,7),(5,8),(5,9),(5,12),(5,13),(5,14),(5,15),(5,16),(5,19),(5,20),(5,21),(5,22),(5,23),(5,26),(5,27),(5,28),(5,29),(5,30),
-- 6월 (20일)
(6,2),(6,3),(6,4),(6,5),(6,9),(6,10),(6,11),(6,12),(6,13),(6,16),(6,17),(6,18),(6,19),(6,20),(6,23),(6,24),(6,25),(6,26),(6,27),(6,30),
-- 7월 (22일)
(7,1),(7,2),(7,3),(7,4),(7,7),(7,8),(7,9),(7,10),(7,11),(7,14),(7,15),(7,16),(7,18),(7,21),(7,22),(7,23),(7,24),(7,25),(7,28),(7,29),(7,30),(7,31),
-- 8월 (20일)
(8,1),(8,4),(8,5),(8,6),(8,7),(8,8),(8,11),(8,12),(8,13),(8,14),(8,18),(8,19),(8,20),(8,21),(8,22),(8,25),(8,26),(8,27),(8,28),(8,29),
-- 9월 (21일)
(9,1),(9,2),(9,3),(9,4),(9,5),(9,8),(9,9),(9,10),(9,11),(9,12),(9,15),(9,16),(9,18),(9,19),(9,22),(9,23),(9,24),(9,25),(9,26),(9,29),(9,30),
-- 10월 (21일)
(10,1),(10,2),(10,6),(10,7),(10,8),(10,10),(10,13),(10,14),(10,15),(10,16),(10,17),(10,20),(10,21),(10,22),(10,23),(10,24),(10,27),(10,28),(10,29),(10,30),(10,31),
-- 11월 (10일 - 현재일까지)
(11,3),(11,4),(11,5),(11,6),(11,7),(11,10),(11,11),(11,12),(11,13),(11,14);

-- 1년 이상 근무자들의 2025년 출근 데이터
INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12341, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12343, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12345, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12347, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 12348, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 123411, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 123412, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 1234123, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

INSERT INTO attendance (employeeId, checkIn, checkOut, attendanceType, note, workHours, overtimeHours, createdAt, updatedAt)
SELECT 25100807, CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 09:00:00'), CONCAT('2025-', LPAD(month, 2, '0'), '-', LPAD(day, 2, '0'), ' 18:00:00'), '정상출근', '정상 근무', 9.0, 1.0, NOW(), NOW() FROM temp_dates_2025;

DROP TEMPORARY TABLE IF EXISTS temp_dates_2025;
