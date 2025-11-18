-- V4.8__add_course_price_and_create_welfare.sql
-- Course 테이블에 price 컬럼 추가 및 Welfare 테이블 생성

-- ============================================
-- 1. Course 테이블에 price 컬럼 추가
-- ============================================
-- ALTER TABLE Courses 
-- ADD COLUMN price DECIMAL(15,2) COMMENT '교육 비용';

-- ============================================
-- 2. Welfare 테이블 생성 (복리후생)
-- ============================================
-- CREATE TABLE welfare (
--     welfareId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '복리후생 ID',
--     employeeId BIGINT NOT NULL COMMENT '직원 ID (FK)',
--     welfareType VARCHAR(50) NOT NULL COMMENT '복리후생 유형',
--     paymentMonth VARCHAR(7) NOT NULL COMMENT '지급 월 (YYYY-MM)',
--     amount DECIMAL(15,2) NOT NULL COMMENT '지급 금액',
--     paymentDate DATE COMMENT '지급일',
--     note VARCHAR(500) COMMENT '비고',
--     approvedBy BIGINT COMMENT '승인자 ID (FK)',
--     isApproved BOOLEAN NOT NULL DEFAULT FALSE COMMENT '승인 여부',
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
--     createdBy VARCHAR(100) COMMENT '생성자',
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
--     updatedBy VARCHAR(100) COMMENT '수정자',
    
--     FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
--     FOREIGN KEY (approvedBy) REFERENCES employees(employeeId),
    
--     INDEX idx_employee_month (employeeId, paymentMonth),
--     INDEX idx_payment_month (paymentMonth),
--     INDEX idx_welfare_type (welfareType)
-- ) COMMENT '복리후생 내역';

-- ============================================
-- 3. 샘플 복리후생 데이터 삽입 (2025년 11월)
-- ============================================
INSERT INTO welfare (employeeId, welfareType, paymentMonth, amount, paymentDate, note, approvedBy, isApproved) VALUES
-- 전체 직원 통신비 (월 5만원)
(1, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12342, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(123559, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12358, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '직무능력 향상 워크샵', 12345, TRUE),
(25100802, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(18061, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(111111, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12341, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12347, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12374, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(102311, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12343, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '직무능력 향상 워크샵', 12345, TRUE),
(123123, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12348, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12396, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(25100807, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(123412, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(1234123, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(123411, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE),
(12345, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '직무능력 향상 워크샵', 12345, TRUE),
(12354, 'EDUCATION', '2025-11', 50000.00, '2025-11-25', '신입사원 온보딩', 12345, TRUE);
