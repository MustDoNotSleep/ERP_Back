# ERP 백엔드 Lambda → FastAPI 마이그레이션 계획 및 TODO 리스트

## 개발 환경 설정
- **언어**: Python 3.13+
- **프레임워크**: FastAPI
- **ORM**: SQLAlchemy 2.0 (MySQL)
- **DB**: MySQL (RDS), DynamoDB
- **인증**: JWT + bcrypt
- **가상환경**: BE/.venv

## 프로젝트 구조
```
BE/
├── .venv/                    # 가상환경
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI 앱 진입점
│   ├── config.py            # 설정 관리
│   ├── database.py          # DB 연결 설정
│   ├── dependencies.py      # 공통 의존성
│   │
│   ├── models/              # SQLAlchemy & DynamoDB 모델
│   │   ├── __init__.py
│   │   ├── mysql/
│   │   │   ├── __init__.py
│   │   │   ├── employee.py
│   │   │   ├── department.py
│   │   │   ├── position.py
│   │   │   ├── military_info.py
│   │   │   ├── salary_info.py
│   │   │   ├── education.py
│   │   │   ├── work_experience.py
│   │   │   ├── certificate.py
│   │   │   └── salary.py
│   │   └── dynamodb/
│   │       ├── __init__.py
│   │       ├── attendance.py
│   │       ├── leave.py
│   │       └── post.py
│   │
│   ├── schemas/             # Pydantic 스키마
│   │   ├── __init__.py
│   │   ├── auth.py
│   │   ├── employee.py
│   │   ├── education.py
│   │   ├── attendance.py
│   │   ├── leave.py
│   │   ├── post.py
│   │   ├── salary.py
│   │   └── common.py
│   │
│   ├── api/                 # API 라우터
│   │   ├── __init__.py
│   │   └── v1/
│   │       ├── __init__.py
│   │       ├── auth.py
│   │       ├── employees.py
│   │       ├── education.py
│   │       ├── attendance.py
│   │       ├── leave.py
│   │       ├── posts.py
│   │       └── salary.py
│   │
│   ├── crud/                # CRUD 로직
│   │   ├── __init__.py
│   │   ├── base.py
│   │   ├── employee.py
│   │   ├── education.py
│   │   ├── attendance.py
│   │   ├── leave.py
│   │   ├── post.py
│   │   └── salary.py
│   │
│   ├── core/                # 핵심 기능
│   │   ├── __init__.py
│   │   ├── security.py      # JWT, bcrypt
│   │   ├── permissions.py   # 권한 검증
│   │   └── exceptions.py
│   │
│   └── utils/               # 유틸리티
│       ├── __init__.py
│       ├── dynamodb.py      # DynamoDB 클라이언트
│       └── response.py      # 공통 응답 포맷
│
├── tests/                   # 테스트 코드
│   ├── __init__.py
│   ├── conftest.py
│   └── api/
│       └── v1/
├── requirements.txt
├── .env.example
├── .env
└── Dockerfile
```

---

## TODO 리스트

### Phase 1: 프로젝트 초기 설정 및 기본 인프라
- [ ] 1.1 가상환경 생성 및 활성화 (BE/.venv)
- [ ] 1.2 requirements.txt 업데이트 및 패키지 설치
  - bcrypt, python-multipart 추가
- [ ] 1.3 프로젝트 폴더 구조 생성
  - app/, app/models/mysql/, app/models/dynamodb/, app/schemas/, app/api/v1/, app/crud/, app/core/, app/utils/, tests/
- [ ] 1.4 .env.example 및 .env 파일 설정
  - MySQL 연결 정보 (DB_HOST, DB_USER, DB_PASSWORD, DB_NAME, DB_PORT)
  - DynamoDB 설정 (AWS_REGION, DYNAMO_TABLE, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
  - JWT 설정 (JWT_SECRET, JWT_ALGORITHM, JWT_EXPIRE_HOURS, JWT_ISSUER, JWT_AUDIENCE)
- [ ] 1.5 config.py 작성 (pydantic-settings 기반)
  - 환경변수 관리 클래스
- [ ] 1.6 database.py 작성 (SQLAlchemy 엔진 및 세션)
  - MySQL 연결 풀 설정 (connectionLimit=10)
  - Base 모델 클래스
- [ ] 1.7 utils/dynamodb.py 작성 (DynamoDB 클라이언트)
  - aioboto3 기반 비동기 클라이언트
  - DynamoDB Document Client 헬퍼 함수
- [ ] 1.8 utils/response.py 작성 (공통 응답 포맷)
  - buildResponse 함수 (statusCode, body)
- [ ] 1.9 main.py 작성 (FastAPI 앱 초기화)
  - CORS 설정
  - Middleware 설정
  - 라우터 등록
- [ ] 1.10 Health check 엔드포인트 구현 (/health, /api/v1/health)
  - MySQL 연결 체크
  - DynamoDB 연결 체크

### Phase 2: 데이터 모델 구현
- [ ] 2.1 MySQL 모델 구현
  - [ ] 2.1.1 Department 모델 (app/models/mysql/department.py)
    - departmentId (PK), departmentName, teamName
  - [ ] 2.1.2 Position 모델 (app/models/mysql/position.py)
    - positionId (PK), positionName, positionLevel
  - [ ] 2.1.3 Employee 모델 (app/models/mysql/employee.py)
    - employeeId (PK), name, nameeng, email, password, phoneNumber, hireDate, rrn, address, addressDetails, familyCertificate, departmentId (FK), positionId (FK), employmentType, username, internalNumber, nationality, quitDate
  - [ ] 2.1.4 MilitaryServiceInfo 모델 (app/models/mysql/military_info.py)
    - militaryId (PK), employeeId (FK), militaryStatus, militaryBranch, militaryRank, militarySpecialty, exemptionReason, serviceStartDate, serviceEndDate
  - [ ] 2.1.5 SalaryInfo 모델 (app/models/mysql/salary_info.py)
    - salaryInfoId (PK), employeeId (FK), bankName, accountNumber, salary
  - [ ] 2.1.6 Education 모델 (app/models/mysql/education.py)
    - educationId (PK), employeeId (FK), schoolName, major, admissionDate, graduationDate, degree, graduationStatus
  - [ ] 2.1.7 WorkExperience 모델 (app/models/mysql/work_experience.py)
    - experienceId (PK), employeeId (FK), companyName, jobTitle, finalPosition, finalSalary, startDate, endDate
  - [ ] 2.1.8 Certificate 모델 (app/models/mysql/certificate.py)
    - certificateId (PK), employeeId (FK), certificateName, issuingAuthority, score, acquisitionDate, expirationDate
  - [ ] 2.1.9 Salary 모델 (app/models/mysql/salary.py)
    - salaryId (PK), employeeId (FK), payDate, baseSalary, allowance, bonus, totalPay, incomeTax, residentTax, socialIns, totalDeductions, netPay
- [ ] 2.2 DynamoDB 모델 클래스 구현 (Pydantic 기반)
  - [ ] 2.2.1 Attendance 모델 (app/models/dynamodb/attendance.py)
    - PK, SK, Type, employeeId, date, clockIn, clockOut, attendanceType
  - [ ] 2.2.2 Leave 모델 (app/models/dynamodb/leave.py)
    - PK, SK, Type, requestId, employeeId, leaveType, startDate, endDate, reason, status, requestDate, requestDays
  - [ ] 2.2.3 Post 모델 (app/models/dynamodb/post.py)
    - PK, SK, Type, postId, employeeId, title, content, attachedFile, createdAt, updatedAt, GSI1PK, GSI1SK

### Phase 3: Pydantic 스키마 정의
- [ ] 3.1 공통 스키마 작성 (app/schemas/common.py)
  - Pagination, ErrorResponse, SuccessResponse, MessageResponse
- [ ] 3.2 Auth 스키마 작성 (app/schemas/auth.py)
  - LoginRequest, LoginResponse, ChangePasswordRequest, TokenData
- [ ] 3.3 Employee 스키마 작성 (app/schemas/employee.py)
  - EmployeeBase, EmployeeCreate, EmployeeRead, EmployeeUpdate, EmployeeList
  - MilitaryInfoCreate, SalaryInfoCreate, EducationCreate (nested), WorkExperienceCreate, CertificateCreate
- [ ] 3.4 Education 스키마 작성 (app/schemas/education.py)
  - EducationBase, EducationCreate, EducationRead, EducationUpdate, EducationList
- [ ] 3.5 Attendance 스키마 작성 (app/schemas/attendance.py)
  - ClockInRequest, ClockOutRequest, AttendanceRecordRead, AttendanceRecordUpdate, AttendanceRecordList
- [ ] 3.6 Leave 스키마 작성 (app/schemas/leave.py)
  - LeaveRequestCreate, LeaveRequestRead, LeaveRequestList, LeaveStatusUpdate
- [ ] 3.7 Post 스키마 작성 (app/schemas/post.py)
  - PostCreate, PostRead, PostUpdate, PostList
- [ ] 3.8 Salary 스키마 작성 (app/schemas/salary.py)
  - SalaryCreate, SalaryRead, SalaryList

### Phase 4: 보안 및 권한 시스템 구현
- [ ] 4.1 보안 함수 구현 (app/core/security.py)
  - hash_password (bcrypt)
  - verify_password (bcrypt)
  - create_access_token (JWT, 3시간 유효)
  - verify_token (JWT)
- [ ] 4.2 권한 검증 함수 구현 (app/core/permissions.py)
  - check_hr_manager (인사팀장: teamName=='인사팀' and positionLevel>=6)
  - check_team_manager (팀장: positionLevel>=6)
  - check_self_or_manager (본인 or 관리자)
- [ ] 4.3 커스텀 예외 클래스 정의 (app/core/exceptions.py)
  - NotFoundException, BadRequestException, UnauthorizedException, ForbiddenException
- [ ] 4.4 의존성 함수 작성 (app/dependencies.py)
  - get_db (MySQL 세션)
  - get_dynamodb_client (DynamoDB 클라이언트)
  - get_current_user (JWT 검증 및 사용자 정보 반환)
  - require_hr_manager
  - require_team_manager

### Phase 5: CRUD 로직 구현 - 사원 관리
- [ ] 5.1 Base CRUD 클래스 구현 (app/crud/base.py)
  - 공통 CRUD 메서드 (get, list, create, update, delete)
- [ ] 5.2 Employee CRUD 구현 (app/crud/employee.py)
  - create_employee (트랜잭션: Employee + MilitaryInfo + SalaryInfo + Education[] + WorkExperience[] + Certificate[])
  - get_employee_by_id
  - get_employee_by_email
  - get_employees_list (권한별 필터링)
  - get_employee_full_details (병역, 급여, 학력, 경력, 자격증 포함)
  - update_employee
  - validate_employee_exists

### Phase 6: CRUD 로직 구현 - 학력 관리
- [ ] 6.1 Education CRUD 구현 (app/crud/education.py)
  - create_education
  - get_education_by_employee_id
  - get_all_education (인사팀 전용, 필터링)
  - update_education
  - delete_education

### Phase 7: CRUD 로직 구현 - 근태/휴가/공지/급여
- [ ] 7.1 Attendance CRUD 구현 (app/crud/attendance.py)
  - clock_in, clock_out, get_attendance_records, update_attendance, delete_attendance
  - DynamoDB 쿼리 로직
- [ ] 7.2 Leave CRUD 구현 (app/crud/leave.py)
  - create_leave_request, get_leave_requests, get_leave_request, update_leave_status
  - DynamoDB 쿼리 로직
- [ ] 7.3 Post CRUD 구현 (app/crud/post.py)
  - create_post, get_posts, get_post, update_post, delete_post
  - DynamoDB 쿼리 로직 (GSI 활용)
- [ ] 7.4 Salary CRUD 구현 (app/crud/salary.py)
  - create_salary_record, get_salary_records, get_salary_record
  - 급여 계산 로직 (totalPay, totalDeductions, netPay)

### Phase 8: API 엔드포인트 구현 - 인증
- [ ] 8.1 Auth API 구현 (app/api/v1/auth.py)
  - [ ] 8.1.1 POST /api/v1/auth/login (로그인)
    - Request: { email, password }
    - Response: { token, user: { employeeId, name, email, teamName, employmentType } }
  - [ ] 8.1.2 POST /api/v1/auth/change-password (비밀번호 변경, JWT 필요)
    - Request: { currentPassword, newPassword }
    - Response: { message }

### Phase 9: API 엔드포인트 구현 - 사원 관리
- [ ] 9.1 Employee API 구현 (app/api/v1/employees.py)
  - [ ] 9.1.1 POST /api/v1/employees (신규 사원 등록, 인사팀장만)
    - Request: EmployeeCreate (복합 데이터)
    - Response: { message, employeeId }
    - 트랜잭션 처리
  - [ ] 9.1.2 GET /api/v1/employees (사원 목록 조회, Manager+)
    - Query: name, employeeId, positionName, teamName
    - Response: { employees: [ EmployeeRead ] }
    - 권한별 필터링
  - [ ] 9.1.3 GET /api/v1/employees/{employee_id} (사원 상세 조회)
    - Response: EmployeeRead (권한에 따라 다른 정보 반환)
  - [ ] 9.1.4 PUT /api/v1/employees/{employee_id} (사원 정보 수정)
    - Request: { phoneNumber, address, internalNumber, bankName, account }
    - Response: { message, updatedFields }

### Phase 10: API 엔드포인트 구현 - 학력 관리
- [ ] 10.1 Education API 구현 (app/api/v1/education.py)
  - [ ] 10.1.1 GET /api/v1/employees/{employee_id}/education (특정 직원 학력 조회, 본인 or 인사팀)
    - Response: [ EducationRead ]
  - [ ] 10.1.2 GET /api/v1/education (전체 학력 조회, 인사팀만)
    - Query: name, employeeId, departmentName
    - Response: [ EducationRead ]
  - [ ] 10.1.3 POST /api/v1/employees/{employee_id}/education (학력 등록, 인사팀만)
    - Request: EducationCreate
    - Response: { message }
  - [ ] 10.1.4 PATCH /api/v1/education/{education_id} (학력 수정, 인사팀만)
    - Request: EducationUpdate
    - Response: { message }
  - [ ] 10.1.5 DELETE /api/v1/education/{education_id} (학력 삭제, 인사팀만)
    - Response: { message }

### Phase 11: API 엔드포인트 구현 - 근태 관리
- [ ] 11.1 Attendance API 구현 (app/api/v1/attendance.py)
  - [ ] 11.1.1 POST /api/v1/attendance/clock-in (출근 기록)
    - Request: { employeeId, attendanceType }
    - Response: { message, clockInTime }
  - [ ] 11.1.2 POST /api/v1/attendance/clock-out (퇴근 기록)
    - Request: { employeeId }
    - Response: { message, clockOutTime }
  - [ ] 11.1.3 GET /api/v1/attendance/records (근태 기록 조회)
    - Query: employeeId, yearMonth (YYYY-MM)
    - Response: [ AttendanceRecordRead ]
  - [ ] 11.1.4 PUT /api/v1/attendance/records (근태 기록 수정)
    - Request: { employeeId, date, clockIn, clockOut, attendanceType }
    - Response: { message }
  - [ ] 11.1.5 DELETE /api/v1/attendance/records (근태 기록 삭제)
    - Request: { employeeId, date, clockInTime }
    - Response: { message }

### Phase 12: API 엔드포인트 구현 - 휴가 관리
- [ ] 12.1 Leave API 구현 (app/api/v1/leave.py)
  - [ ] 12.1.1 POST /api/v1/leave/requests (휴가 신청)
    - Request: { employeeId, leaveType, startDate, endDate, reason, requestDays }
    - Response: { message, requestId }
  - [ ] 12.1.2 GET /api/v1/leave/requests (휴가 신청 목록 조회)
    - Query: employeeId (optional), status (optional)
    - Response: [ LeaveRequestRead ]
  - [ ] 12.1.3 GET /api/v1/leave/requests/{request_id} (휴가 신청 상세 조회)
    - Response: LeaveRequestRead
  - [ ] 12.1.4 PATCH /api/v1/leave/requests/{request_id}/status (휴가 승인/거절, Manager)
    - Request: { status: "Approved" | "Rejected" }
    - Response: { message }

### Phase 13: API 엔드포인트 구현 - 공지사항 관리
- [ ] 13.1 Post API 구현 (app/api/v1/posts.py)
  - [ ] 13.1.1 POST /api/v1/posts (공지사항 생성, Manager+)
    - Request: { employeeId, title, content, attachedFile }
    - Response: { message, postId }
  - [ ] 13.1.2 GET /api/v1/posts (공지사항 목록 조회)
    - Query: page, limit
    - Response: { items: [ PostRead ], total, page, limit }
  - [ ] 13.1.3 GET /api/v1/posts/{post_id} (공지사항 상세 조회)
    - Response: PostRead
  - [ ] 13.1.4 PUT /api/v1/posts/{post_id} (공지사항 수정, Author/HR)
    - Request: { title, content, attachedFile }
    - Response: { message }
  - [ ] 13.1.5 DELETE /api/v1/posts/{post_id} (공지사항 삭제, Author/HR)
    - Response: { message }

### Phase 14: API 엔드포인트 구현 - 급여 관리
- [ ] 14.1 Salary API 구현 (app/api/v1/salary.py)
  - [ ] 14.1.1 POST /api/v1/salary/records (급여 정보 생성, HR Manager)
    - Request: { employeeId, payDate, baseSalary, allowance, bonus, incomeTax, residentTax, socialIns }
    - Response: { message, netPay }
  - [ ] 14.1.2 GET /api/v1/salary/records (급여 정보 조회, Self/HR)
    - Query: employeeId, startDate, endDate
    - Response: [ SalaryRead ]
  - [ ] 14.1.3 GET /api/v1/salary/records/{record_id} (급여 상세 조회, Self/HR)
    - Response: SalaryRead

### Phase 15: 테스트 구현
- [ ] 15.1 테스트 인프라 구축
  - [ ] 15.1.1 tests/conftest.py (테스트 픽스처 및 설정)
    - TestClient, 테스트 DB 세션, DynamoDB 로컬 설정, 테스트 사용자
  - [ ] 15.1.2 pytest.ini (pytest 설정)
- [ ] 15.2 API 엔드포인트 테스트 (pytest)
  - [ ] 15.2.1 tests/api/v1/test_auth.py (인증 API 테스트)
    - 로그인, 비밀번호 변경 테스트
  - [ ] 15.2.2 tests/api/v1/test_employees.py (사원 API 테스트)
    - 등록, 조회, 수정 테스트, 권한 테스트
  - [ ] 15.2.3 tests/api/v1/test_education.py (학력 API 테스트)
    - CRUD 테스트, 권한 테스트
  - [ ] 15.2.4 tests/api/v1/test_attendance.py (근태 API 테스트)
    - 출근, 퇴근, 조회, 수정, 삭제 테스트
  - [ ] 15.2.5 tests/api/v1/test_leave.py (휴가 API 테스트)
    - 신청, 조회, 상태 변경 테스트
  - [ ] 15.2.6 tests/api/v1/test_posts.py (공지사항 API 테스트)
    - 생성, 조회, 수정, 삭제 테스트
  - [ ] 15.2.7 tests/api/v1/test_salary.py (급여 API 테스트)
    - 생성, 조회 테스트
- [ ] 15.3 CRUD 로직 단위 테스트
  - [ ] 15.3.1 tests/crud/test_crud_employee.py
  - [ ] 15.3.2 tests/crud/test_crud_education.py
  - [ ] 15.3.3 tests/crud/test_crud_attendance.py
  - [ ] 15.3.4 tests/crud/test_crud_leave.py
  - [ ] 15.3.5 tests/crud/test_crud_post.py
  - [ ] 15.3.6 tests/crud/test_crud_salary.py
- [ ] 15.4 통합 테스트 (tests/test_integration.py)
  - 사원 등록 → 로그인 → 출근 → 휴가 신청 → 급여 조회 워크플로우

### Phase 16: 문서화 및 최적화
- [ ] 16.1 OpenAPI/Swagger 문서 검토 및 수정
  - 상세한 API 설명 추가
  - 태그 정의 (인증, 사원, 학력, 근태, 휴가, 공지사항, 급여)
  - 인증 방법 문서화 (Bearer Token)
  - Request/Response 예시 추가
- [ ] 16.2 에러 핸들링 개선
  - Exception 핸들러 (app/main.py)
  - 프로덕션/개발 환경별 오류 메시지 분기
  - 구조화된 에러 응답
- [ ] 16.3 로깅 설정
  - loguru 기반 구조화된 로깅
  - 파일 로깅 (일별 로테이션)
  - 에러 로그 별도 관리
  - API 요청/응답 로깅
- [ ] 16.4 성능 최적화
  - DB 쿼리 최적화 (N+1 문제 해결)
  - 인덱스 검토
  - Connection Pool 튜닝

### Phase 17: Dockerfile 및 배포 준비
- [ ] 17.1 Dockerfile 작성
  - 멀티 스테이지 빌드
  - Health check 설정
  - .dockerignore 파일
  - 보안 설정 (non-root 사용자)
- [ ] 17.2 docker-compose.yml 작성
  - 백엔드 서비스 설정
  - MySQL 서비스 (로컬 개발용)
  - DynamoDB Local 서비스 (로컬 개발용)
  - 네트워크 및 볼륨 설정
- [ ] 17.3 환경변수 문서화
  - .env.example 파일 작성
  - 환경별 설정 예시 (개발, Docker, 프로덕션)
  - 필수/선택 변수 구분
- [ ] 17.4 배포 스크립트 작성
  - scripts/dev-start.sh (개발 환경 시작)
  - scripts/prod-deploy.sh (프로덕션 배포)
  - scripts/migrate-passwords.py (평문 비밀번호 → bcrypt 마이그레이션)
- [ ] 17.5 문서화
  - README.md 작성
  - 빠른 시작 가이드
  - API 사용 가이드
  - 배포 가이드

### Phase 18: Lambda → FastAPI 트래픽 전환
- [ ] 18.1 스테이징 환경 배포
  - 스테이징 서버 구축
  - 통합 테스트 실행
- [ ] 18.2 API Gateway 설정 변경
  - Lambda 함수 → FastAPI 서버 엔드포인트
  - 점진적 트래픽 전환 (카나리 배포)
- [ ] 18.3 모니터링 및 로그 분석
  - CloudWatch 로그 확인
  - 에러율 모니터링
  - 응답 시간 모니터링
- [ ] 18.4 Lambda 함수 제거 (검증 완료 후)

---

## 개발 원칙
1. **모듈화**: 각 기능을 독립적인 모듈로 분리
2. **절대경로 임포트**: `from app.models.mysql.employee import Employee` 형태로 작성
3. **`__init__.py` 관리**: 각 디렉토리에 적절한 `__init__.py` 작성
4. **타입 힌팅**: 모든 함수에 타입 힌팅 적용
5. **에러 핸들링**: 적절한 HTTP 상태 코드 및 에러 메시지
6. **보안**: bcrypt 해싱, SQL Injection 방지, 입력값 검증, 개인정보 보호
7. **권한 검증**: 모든 API에 권한 체크 적용
8. **트랜잭션**: 여러 테이블 작업 시 트랜잭션 사용
9. **비동기 처리**: DynamoDB 작업은 aioboto3 사용

---

## 주요 패키지
```txt
fastapi==0.109.0
uvicorn[standard]==0.27.0
sqlalchemy==2.0.25
pymysql==1.1.0
cryptography==41.0.7
pydantic==2.5.3
pydantic-settings==2.1.0
python-jose[cryptography]==3.3.0
passlib[bcrypt]==1.7.4
boto3==1.34.0
aioboto3==12.3.0
httpx==0.26.0
pytest==7.4.4
pytest-asyncio==0.23.3
pytest-cov==4.1.0
loguru==0.7.2
black==24.1.1
flake8==7.0.0
mypy==1.8.0
python-multipart==0.0.6
```

---

## 권한 상수
```python
# app/core/permissions.py
MANAGER_LEVEL = 6
HR_TEAM_NAME = "인사팀"
```

---

## 다음 단계
위 TODO 리스트의 Phase 1부터 순차적으로 진행하며, 각 Phase 완료 시마다 체크마크 표시 및 구현 검증

특정 Phase를 진행하려면:
- "Phase 1 진행해줘"
- "Phase 1-3까지 진행해줘"
- "Phase 8의 8.1.1 진행해줘"

형식으로 요청하세요.
