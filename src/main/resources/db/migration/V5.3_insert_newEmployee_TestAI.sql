-- AI 테스트에서 중복으로 떠서 새로 추가하는 김대리 데이터

INSERT INTO employees (
	employeeId, name, nameEng, password, rrn,
    email, address, addressDetails,
    phone, birthDate, hireDate, quitDate,
    internalNumber, familyCertificate,
    departmentId, positionId,
    employmentType, nationality,
    createdAt, updatedAt, createdBy, updatedBy
) VALUES (
	'12344',
    '김대리',
    'Kim Test',
    '$2a$10$SNTatAB8eQKwIm8MKG8Qqe4xIk1.Fgj88fN0fxxqk7lgmjkiujq5O', -- 나중에 수정하면 됨
    '990101-1234567',
    'kimtest@apex.com',
    '서울특별시 강남구 테스터로 123',
    '101동 202호',
    '010-9999-8888',
    '1999-01-01',
    '2025-01-02',
    NULL,
    '999999',
    NULL,
    2,      -- 인사팀
    13,     -- 대리
    '정규직',
    '내국인',
    NOW(), NOW(),
    '12341', '12341'
);

INSERT INTO WorkEvaluations (
    employeeId, evaluation_year, evaluation_quarter,
    attitude_score, achievement_score, collaboration_score,
    contribution_grade, total_grade, status, evaluatorId
) VALUES

-- 평가1
(12344, 2025, 4, 4, 5, 5, 'A', 'T', '제출완료', 12341),

-- 평가2
(12344, 2025, 4, 5, 4, 5, 'A', 'T', '제출완료', 12342),

-- 평가3
(12344, 2025, 4, 4, 4, 5, 'A', 'T', '제출완료', 12343),

-- 평가4
(12344, 2025, 4, 5, 5, 4, 'A', 'T', '제출완료', 1);
