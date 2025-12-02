CREATE TABLE rewards (
    -- 1. 기본 키 (PK)
    rewardId BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '포상 ID',

    -- 2. 대상자 정보 (FK)
    employeeId BIGINT NOT NULL COMMENT '포상 대상자 사번',
    
    -- 3. 포상 상세 정보
    rewardDate DATE NOT NULL COMMENT '요청일/추천일',
    rewardType VARCHAR(50) NOT NULL COMMENT '포상 종류 (ENUM)',
    rewardItem VARCHAR(50) NOT NULL COMMENT '포상 형태 (ENUM)',
    rewardValue VARCHAR(100) NULL COMMENT '포상 이유(ENUM)',
    amount DECIMAL(15,2) NULL COMMENT '포상 금액',
    reason TEXT NULL COMMENT '사유',

    -- 4. 결재 상태
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL COMMENT '결재 상태',

    -- 5. 승인자 정보 (FK)
    approverId BIGINT NULL COMMENT '승인자 사번',
    approvedAt DATETIME NULL COMMENT '승인 일시',

    -- 6. 시스템 관리 컬럼
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    -- 7. 외래 키 제약조건
    -- (주의: employees 테이블의 PK가 employeeId 라고 가정했습니다)
    CONSTRAINT fk_rewards_employee FOREIGN KEY (employeeId) REFERENCES employees (employeeId),
    CONSTRAINT fk_rewards_approver FOREIGN KEY (approverId) REFERENCES employees (employeeId)
);

INSERT INTO rewards 
(employeeId, rewardDate, rewardType, rewardItem, rewardValue, amount, reason, status, approverId, approvedAt) 
VALUES 
-- 1. [최신입(1)] 승인: 한팀장(123411) 승인
(1, '2025-01-10', 'BEST_EMPLOYEE', 'MONEY', 'TEAM_CONTRIBUTION', 300000, '신입사원 온보딩 교육 우수 수료', 'APPROVED', 123411, '2025-01-12 10:00:00'),

-- 2. [최인사(12341)] 대기
(12341, '2025-01-15', 'CONTRIBUTION', 'POINT', 'ETC', 50000, '사내 문화 개선 아이디어 제안', 'PENDING', NULL, NULL),

-- 3. [박인턴(12342)] 반려: 안수석(12354) 반려
(12342, '2025-01-20', 'SPECIAL', 'VACATION', 'CORE_TECH', 0, '인턴 프로젝트 조기 달성 (반려: 아직 수습 기간임)', 'REJECTED', 12354, '2025-01-21 14:30:00'),

-- 4. [박책임(12343)] 승인: 한팀장(123411) 승인
(12343, '2025-02-01', 'BEST_EMPLOYEE', 'PLAQUE', 'LONG_SERVICE', 0, '근속 10주년 기념패', 'APPROVED', 123411, '2025-02-05 09:00:00'),

-- 5. [김대리(12344)] 대기
(12344, '2025-02-10', 'CONTRIBUTION', 'MONEY', 'TEAM_CONTRIBUTION', 100000, '프로젝트 A 성공적 런칭 기여', 'PENDING', NULL, NULL),

-- 6. [김수석(12345)] 승인: 한팀장(123411) 승인
(12345, '2025-02-15', 'SPECIAL', 'MONEY', 'CORE_TECH', 1000000, '핵심 특허 출원 승인', 'APPROVED', 123411, '2025-02-16 11:20:00'),

-- 7. [윤대리(12347)] 대기
(12347, '2025-03-01', 'CONTRIBUTION', 'POINT', 'ETC', 30000, '동호회 활동 활성화 기여', 'PENDING', NULL, NULL),

-- 8. [홍선임(12348)] 승인: 안수석(12354) 승인
(12348, '2025-03-10', 'BEST_EMPLOYEE', 'VACATION', 'TEAM_CONTRIBUTION', 0, '1분기 최우수 사원 선정', 'APPROVED', 12354, '2025-03-15 15:40:00'),

-- 9. [정사원(12358)] 반려: 한팀장(123411) 반려
(12358, '2025-03-20', 'SPECIAL', 'MONEY', 'ETC', 500000, '개인 사유로 인한 포상 신청 (반려: 사유 불충분)', 'REJECTED', 123411, '2025-03-21 13:00:00'),

-- 10. [박대리(12374)] 대기
(12374, '2025-04-01', 'CONTRIBUTION', 'MONEY', 'CORE_TECH', 200000, '레거시 코드 리팩토링 완료', 'PENDING', NULL, NULL),

-- 11. [김선임(12396)] 승인: 한팀장(123411) 승인
(12396, '2025-04-15', 'SPECIAL', 'PLAQUE', 'LONG_SERVICE', 0, '근속 5주년 기념', 'APPROVED', 123411, '2025-04-20 10:30:00'),

-- 12. [송은우(18061)] 대기
(18061, '2025-05-01', 'BEST_EMPLOYEE', 'POINT', 'TEAM_CONTRIBUTION', 50000, '팀 분위기 메이커상', 'PENDING', NULL, NULL),

-- 13. [이카페(102311)] 승인: 안수석(12354) 승인
(102311, '2025-05-10', 'CONTRIBUTION', 'VACATION', 'ETC', 0, '사내 카페테리아 개선 제안', 'APPROVED', 12354, '2025-05-12 16:00:00'),

-- 14. [홍신입(111111)] 대기
(111111, '2025-06-01', 'SPECIAL', 'MONEY', 'CORE_TECH', 300000, '신기술 도입 POC 성공', 'PENDING', NULL, NULL),

-- 15. [이하늘(123123)] 승인: 한팀장(123411) 승인
(123123, '2025-06-15', 'BEST_EMPLOYEE', 'MONEY', 'TEAM_CONTRIBUTION', 500000, '상반기 영업 목표 초과 달성', 'APPROVED', 123411, '2025-06-20 09:30:00'),

-- 16. [서과장(123412)] 반려: 한팀장(123411) 반려
(123412, '2025-07-01', 'CONTRIBUTION', 'VACATION', 'ETC', 0, '휴가 신청 중복 (반려)', 'REJECTED', 123411, '2025-07-02 11:00:00'),

-- 17. [정혜영(123559)] 대기
(123559, '2025-07-10', 'SPECIAL', 'POINT', 'LONG_SERVICE', 100000, '모범 사원 표창', 'PENDING', NULL, NULL),

-- 18. [이분홍(1234123)] 승인: 안수석(12354) 승인
(1234123, '2025-08-01', 'BEST_EMPLOYEE', 'PLAQUE', 'CORE_TECH', 0, '올해의 개발자상 수상', 'APPROVED', 12354, '2025-08-05 14:00:00'),

-- 19. [최사원(25100802)] 대기
(25100802, '2025-08-15', 'CONTRIBUTION', 'MONEY', 'TEAM_CONTRIBUTION', 150000, '멘토링 프로그램 우수 멘티', 'PENDING', NULL, NULL),

-- 20. [이연구(25100807)] 대기
(25100807, '2025-09-01', 'SPECIAL', 'VACATION', 'CORE_TECH', 0, '데이터 분석 시스템 구축 기여', 'PENDING', NULL, NULL);

INSERT INTO rewards 
(employeeId, rewardDate, rewardType, rewardItem, rewardValue, amount, reason, status, approverId, approvedAt) 
VALUES 
-- 21. [최인사(12341)] 승인: 한팀장 승인
(12341, '2025-10-01', 'CONTRIBUTION', 'MONEY', 'ETC', 100000, '사내 동호회 운영 우수', 'APPROVED', 123411, '2025-10-05 09:30:00'),

-- 22. [박인턴(12342)] 대기
(12342, '2025-10-05', 'SPECIAL', 'POINT', 'TEAM_CONTRIBUTION', 30000, '인턴 과제 발표회 1등', 'PENDING', NULL, NULL),

-- 23. [박책임(12343)] 반려: 안수석 반려
(12343, '2025-10-10', 'BEST_EMPLOYEE', 'VACATION', 'CORE_TECH', 0, '프로젝트 일정 지연으로 인한 반려', 'REJECTED', 12354, '2025-10-11 14:00:00'),

-- 24. [김대리(12344)] 승인: 안수석 승인
(12344, '2025-10-15', 'CONTRIBUTION', 'PLAQUE', 'LONG_SERVICE', 0, '모범 사원상 (고객 칭찬 접수)', 'APPROVED', 12354, '2025-10-16 10:20:00'),

-- 25. [김수석(12345)] 대기
(12345, '2025-10-20', 'SPECIAL', 'MONEY', 'CORE_TECH', 500000, '차세대 플랫폼 아키텍처 설계', 'PENDING', NULL, NULL),

-- 26. [윤대리(12347)] 승인: 한팀장 승인
(12347, '2025-10-25', 'BEST_EMPLOYEE', 'POINT', 'ETC', 200000, '전사 워크샵 기획 및 운영', 'APPROVED', 123411, '2025-10-28 16:00:00'),

-- 27. [홍선임(12348)] 대기
(12348, '2025-11-01', 'CONTRIBUTION', 'VACATION', 'TEAM_CONTRIBUTION', 0, '신규 입사자 OJT 멘토링 우수', 'PENDING', NULL, NULL),

-- 28. [안수석(12354)] 승인: 한팀장 승인 (수석도 상 받을 수 있음)
(12354, '2025-11-05', 'SPECIAL', 'PLAQUE', 'LONG_SERVICE', 0, '근속 15주년 공로패', 'APPROVED', 123411, '2025-11-06 09:00:00'),

-- 29. [정사원(12358)] 대기
(12358, '2025-11-10', 'BEST_EMPLOYEE', 'MONEY', 'ETC', 300000, '업무 프로세스 자동화 툴 개발', 'PENDING', NULL, NULL),

-- 30. [박대리(12374)] 반려: 한팀장 반려
(12374, '2025-11-15', 'CONTRIBUTION', 'POINT', 'TEAM_CONTRIBUTION', 50000, '단순 업무 지원 (포상 기준 미달)', 'REJECTED', 123411, '2025-11-16 13:30:00'),

-- 31. [김선임(12396)] 대기
(12396, '2025-11-20', 'SPECIAL', 'MONEY', 'CORE_TECH', 1000000, '보안 취약점 조기 발견 및 조치', 'PENDING', NULL, NULL),

-- 32. [송은우(18061)] 승인: 안수석 승인
(18061, '2025-11-25', 'BEST_EMPLOYEE', 'VACATION', 'ETC', 0, '하반기 베스트 드레서상', 'APPROVED', 12354, '2025-11-26 15:00:00'),

-- 33. [이카페(102311)] 대기
(102311, '2025-12-01', 'CONTRIBUTION', 'POINT', 'TEAM_CONTRIBUTION', 30000, '쾌적한 휴게 공간 관리', 'PENDING', NULL, NULL),

-- 34. [홍신입(111111)] 대기
(111111, '2025-12-05', 'SPECIAL', 'MONEY', 'ETC', 200000, '연말 송년회 장기자랑 우승', 'PENDING', NULL, NULL),

-- 35. [이하늘(123123)] 승인: 한팀장 승인
(123123, '2025-12-10', 'BEST_EMPLOYEE', 'PLAQUE', 'CORE_TECH', 0, '올해의 프로젝트 리더상', 'APPROVED', 123411, '2025-12-12 11:00:00'),

-- 36. [서과장(123412)] 대기
(123412, '2025-12-15', 'CONTRIBUTION', 'MONEY', 'TEAM_CONTRIBUTION', 100000, '부서 간 갈등 해결 및 중재', 'PENDING', NULL, NULL),

-- 37. [정혜영(123559)] 승인: 안수석 승인
(123559, '2025-12-20', 'SPECIAL', 'VACATION', 'LONG_SERVICE', 0, '근속 7주년 리프레시 휴가', 'APPROVED', 12354, '2025-12-21 14:00:00'),

-- 38. [이분홍(1234123)] 대기
(1234123, '2025-12-24', 'BEST_EMPLOYEE', 'POINT', 'ETC', 50000, '크리스마스 이벤트 기획', 'PENDING', NULL, NULL),

-- 39. [최사원(25100802)] 반려: 안수석 반려
(25100802, '2025-12-26', 'CONTRIBUTION', 'MONEY', 'TEAM_CONTRIBUTION', 300000, '업무 성과 증빙 부족', 'REJECTED', 12354, '2025-12-27 10:00:00'),

-- 40. [이연구(25100807)] 대기
(25100807, '2025-12-28', 'SPECIAL', 'MONEY', 'CORE_TECH', 2000000, 'AI 모델 성능 20% 향상', 'PENDING', NULL, NULL);