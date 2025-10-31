-- V2.1__insert_test_data.sql
-- employeeId 12341 (최인사) 직원에 대한 테스트 데이터 삽입

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 학력(Education) 데이터
INSERT INTO education (educationId, schoolName, major, degree, graduationDate, employeeId, createdAt, updatedAt) VALUES
(1, '서울대학교', '경영학과', 'BACHELOR', '2008-02-20', 12341, NOW(), NOW()),
(2, '연세대학교', '인적자원관리학과', 'MASTER', '2010-08-15', 12341, NOW(), NOW());

-- 2. 경력(WorkExperience) 데이터
INSERT INTO workExperience (experienceId, companyName, jobTitle, finalPosition, finalSalary, startDate, endDate, employeeId, createdAt, updatedAt) VALUES
(1, 'ABC 컨설팅', '인사담당', '과장', 3500, '2008-03-01', '2010-09-30', 12341, NOW(), NOW()),
(2, 'XYZ 그룹', 'HR Manager', '부장', 4500, '2010-10-01', '2010-10-09', 12341, NOW(), NOW());

-- 3. 자격증(Certificate) 데이터
INSERT INTO certificates (certificateId, certificateName, issuingAuthority, acquisitionDate, score, employeeId, createdAt, updatedAt) VALUES
(1, '인적자원관리사 1급', '한국산업인력공단', '2009-06-15', NULL, 12341, NOW(), NOW()),
(2, '사회복지사 2급', '한국사회복지사협회', '2010-03-20', NULL, 12341, NOW(), NOW()),
(3, 'TOEIC', 'ETS', '2009-11-10', '900', 12341, NOW(), NOW());

-- 4. 병역사항(MilitaryServiceInfo) 데이터
INSERT INTO military_service_info (militaryInfoId, militaryStatus, militaryBranch, militaryRank, serviceStartDate, serviceEndDate, employeeId, createdAt, updatedAt) VALUES
(1, 'HYEON_YEOK', 'YUK_GOON', 'BYEONG_JANG', '2005-03-02', '2007-01-15', 12341, NOW(), NOW());

-- 5. 교육과정(Course) 데이터 (회사 내부 교육과정)
INSERT INTO courses (courseId, courseName, completionCriteria, capacity, courseType, startDate, endDate, objective, creator, createdAt, updatedAt) VALUES
(1, '신입사원 온보딩', '80% 이상 출석', 20, 'MANDATORY', '2025-11-01', '2025-11-05', '신입사원 대상 회사 소개 및 기본 교육', 12341, NOW(), NOW()),
(2, '리더십 개발과정', '전체 과정 이수', 15, 'OPTIONAL', '2025-11-15', '2025-11-17', '중간관리자 대상 리더십 교육', 12341, NOW(), NOW()),
(3, '직무능력 향상 워크샵', '평가 통과', 30, 'OPTIONAL', '2025-12-01', '2025-12-03', '전사원 대상 직무 교육', 12341, NOW(), NOW());

-- 6. 교육과정 신청(CourseApplication) 데이터
INSERT INTO course_applications (applicationId, applicationDate, status, employeeId, courseId, createdAt, updatedAt) VALUES
(1, NOW(), 'APPROVED', 12341, 1, NOW(), NOW()),
(2, NOW(), 'PENDING', 12341, 2, NOW(), NOW());

-- 7. 증명서 발급(DocumentApplication) 데이터
INSERT INTO document_applications (documentId, documentType, purpose, applicationDate, documentStatus, language, employeeId, createdAt, updatedAt) VALUES
(1, 'CERTIFICATE_OF_EMPLOYMENT', '은행 대출용', NOW(), 'APPROVED', 'KOREAN', 12341, NOW(), NOW()),
(2, 'CERTIFICATE_OF_CAREER', '자격증 신청용', NOW(), 'APPROVED', 'KOREAN', 12341, NOW(), NOW()),
(3, 'CERTIFICATE_OF_INCOME', '아파트 계약용', NOW(), 'PENDING', 'KOREAN', 12341, NOW(), NOW());

-- 8. 인사발령(AppointmentRequest) 데이터
INSERT INTO appointment_requests (appointmentRequestId, targetEmployeeId, requestingEmployeeId, appointmentType, newDepartmentId, effectiveStartDate, reason, status, requestDate, createdAt, updatedAt) VALUES
(1, 12341, 12341, 'TRANSFER', 2, '2010-10-10', '인사팀 전보 발령', 'APPROVED', NOW(), NOW(), NOW());

-- 9. 급여계좌(SalaryInfo) 데이터
INSERT INTO salary_info (salaryInfoId, bankName, accountNumber, accountHolder, employeeId, createdAt, updatedAt) VALUES
(1, 'KOOKMIN_BANK', '123-456-789012', '최인사', 12341, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
