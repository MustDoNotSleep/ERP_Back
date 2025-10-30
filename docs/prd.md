# ERP 백엔드 Lambda → Spring Boot 마이그레이션 PRD

## 1. 프로젝트 개요

### 1.1 목적
현재 AWS Lambda 함수로 구현된 ERP 백엔드 시스템을 Spring Boot (Java) 기반의 EC2 통합 서버로 마이그레이션하여 관리 효율성을 높이고, 확장 가능한 아키텍처를 구축합니다.

### 1.2 현재 시스템 분석

#### 기존 아키텍처
- **플랫폼**: AWS Lambda (Node.js ES6 Modules)
- **데이터베이스**:
  - DynamoDB: 근태, 휴가, 공지사항
  - MySQL (RDS): 사원 정보, 급여, 학력, 경력, 자격증, 병역정보
- **구조**: Handler → Service → Database Client
- **인증**: JWT (평문 비밀번호, 3시간 유효)
- **권한 관리**:
  - 일반 사원 / 팀장 (positionLevel >= 6) / 인사팀장 (인사팀 + Level >= 6)

#### 기존 기능 분석

##### 1) 인증 및 권한 관리 (Auth)
- **Handler**: `handler/loginHandler.js`, `handler/changePasswordHandler.js`
- **데이터베이스**: MySQL (RDS)
- **주요 기능**:
  - `POST /login`: 로그인 (이메일/비밀번호)
    - JWT 생성 (employeeId, email, name, positionLevel, employmentType, teamName)
    - 유효기간: 3시간
  - `POST /change-password`: 비밀번호 변경 (JWT 인증 필요)
    - 현재 비밀번호 확인 후 변경
- **보안 정책**:
  - 평문 비밀번호 저장 (보안 개선 필요)
  - JWT SECRET: 환경변수
  - Issuer: 'YourCompanyAuth', Audience: 'YourApiGateway'

##### 2) 사원 관리 (Employee)
- **Handler**:
  - `handler/registerEmployeeHandler.js`: 사원 등록
  - `handler/getEmployeeHandler.js`: 사원 조회
  - `handler/updateEmployeeHandler.js`: 사원 정보 수정
- **데이터베이스**: MySQL (RDS)
- **주요 기능**:
  - `POST /employees`: 신규 사원 등록 (인사팀장만 가능)
    - 기본정보, 병역정보, 급여정보, 학력, 경력, 자격증 일괄 등록 (트랜잭션)
  - `GET /employees`: 사원 목록 조회
    - 인사팀장: 전체 조회
    - 팀장: 자기 부서만 조회
    - 일반 사원: 조회 불가
    - 필터링: name, employeeId, positionName, teamName
  - `GET /employees/{employeeId}`: 사원 상세 조회
    - 본인: 전체 정보 (병역, 급여, 학력, 경력, 자격증 포함)
    - 인사팀장: 타인 기본정보만
    - 팀장: 같은 부서만 전체 정보
  - `PUT /employees/{employeeId}`: 사원 정보 수정
    - 수정 가능 필드: phoneNumber, address, internalNumber, bankName, account
- **데이터 모델** (MySQL):
  ```sql
  Employees:
  - employeeId (PK): varchar
  - name: varchar
  - nameeng: varchar
  - email: varchar (unique)
  - password: varchar (평문)
  - phoneNumber: varchar
  - hireDate: date
  - rrn: varchar (주민등록번호, 암호화 권장)
  - address: varchar
  - addressDetails: varchar
  - familyCertificate: varchar
  - departmentId: FK → Department
  - positionId: FK → Positions
  - employmentType: varchar (정규직, 계약직 등)
  - username: varchar
  - internalNumber: varchar
  - nationality: varchar (내국인/외국인)
  - quitDate: date (퇴사일, NULL이면 재직중)

  Department:
  - departmentId (PK)
  - departmentName: varchar
  - teamName: varchar

  Positions:
  - positionId (PK)
  - positionName: varchar
  - positionLevel: int (6 이상이 관리자)

  MilitaryServiceInfo:
  - militaryId (PK, auto_increment)
  - employeeId (FK → Employees)
  - militaryStatus: varchar (군필, 미필, 면제 등)
  - militaryBranch: varchar (육군, 해군 등)
  - militaryRank: varchar
  - militarySpecialty: varchar
  - exemptionReason: varchar
  - serviceStartDate: date
  - serviceEndDate: date

  SalaryInfo:
  - salaryInfoId (PK, auto_increment)
  - employeeId (FK → Employees)
  - bankName: varchar
  - accountNumber: varchar
  - salary: decimal

  Education:
  - educationId (PK, auto_increment)
  - employeeId (FK → Employees)
  - schoolName: varchar
  - major: varchar
  - admissionDate: date
  - graduationDate: date
  - degree: varchar (고졸, 학사, 석사 등)
  - graduationStatus: varchar (졸업, 재학, 중퇴 등)

  WorkExperience:
  - experienceId (PK, auto_increment)
  - employeeId (FK → Employees)
  - companyName: varchar
  - jobTitle: varchar
  - finalPosition: varchar
  - finalSalary: decimal
  - startDate: date
  - endDate: date

  Certificates:
  - certificateId (PK, auto_increment)
  - employeeId (FK → Employees)
  - certificateName: varchar
  - issuingAuthority: varchar
  - score: varchar
  - acquisitionDate: date
  - expirationDate: date


##### 3) 학력 관리 (Education)
- **Handler**: `handler/educationHandler.js`
- **Service**: `services/educationService.js`
- **데이터베이스**: MySQL (RDS)
- **주요 기능**:
  - `GET /employees/{employeeId}/education`: 특정 직원 학력 조회 (본인 or 인사팀)
  - `GET /education`: 전체 학력 조회 (인사팀만, 필터링: name, employeeId, departmentName)
  - `POST /employees/{employeeId}/education`: 학력 등록 (인사팀만)
  - `PATCH /education/{educationId}`: 학력 수정 (인사팀만)
  - `DELETE /education/{educationId}`: 학력 삭제 (인사팀만)

##### 4) 근태 관리 (Attendance)
- **Handler**: `handler/attendanceHandler.js` (주석처리됨, erp.js에서 통합 관리 가능)
- **Service**: `services/AttendanceService.js`
- **데이터베이스**: DynamoDB
- **주요 기능**:
  - `POST /attendance/clock-in`: 출근 기록 생성
  - `POST /attendance/clock-out`: 퇴근 기록 업데이트
  - `GET /attendance/records`: 월별 근태 기록 조회 (employeeId, yearMonth)
  - `PUT /attendance/records`: 근태 기록 수정
  - `DELETE /attendance/records`: 근태 기록 삭제
- **데이터 모델**:
  ```
  PK: EMP#{employeeId}
  SK: ATTENDANCE#{date}#{clockInTime}
  - Type: 'Attendance'
  - employeeId: string
  - date: string (YYYY-MM-DD)
  - clockIn: string (HH:MM:SS)
  - clockOut: string (HH:MM:SS, optional)
  - attendanceType: string (정상근무, 지각, 조퇴 등)
  ```

##### 5) 휴가 관리 (Leave)
- **Handler**: `handler/leaveHandler.js`
- **Service**: `services/LeaveService.js`
- **데이터베이스**: DynamoDB
- **주요 기능**:
  - `POST /leave/requests`: 휴가 신청
- **데이터 모델**:
  ```
  PK: EMP#{employeeId}
  SK: LEAVE#{requestId}
  - Type: 'LeaveRequest'
  - requestId: string
  - employeeId: string
  - leaveType: string (연차, 병가, 경조사 등)
  - startDate: string (YYYY-MM-DD)
  - endDate: string (YYYY-MM-DD)
  - reason: string
  - status: string (Pending, Approved, Rejected)
  - requestDate: string (YYYY-MM-DD)
  - requestDays: number
  ```

##### 6) 공지사항 관리 (Post)
- **Handler**: `handler/postHandler.js`
- **Service**: `services/PostService.js`
- **데이터베이스**: DynamoDB
- **주요 기능**:
  - `POST /posts`: 공지사항 생성
- **데이터 모델**:
  ```
  PK: POST#{timestamp}
  SK: METADATA
  - Type: 'Post'
  - postId: string
  - employeeId: string (작성자)
  - title: string
  - content: string
  - attachedFile: string (optional)
  - createdAt: ISO string
  - updatedAt: ISO string
  GSI1PK: TYPE#POST (전체 공지 조회용)
  GSI1SK: createdAt
  ```

##### 7) 급여 관리 (Salary)
- **Handler**: `handler/salaryHandler.js`
- **Service**: `services/SalaryService.js`
- **데이터베이스**: MySQL (RDS)
- **주요 기능**:
  - `POST /salary/records`: 급여 정보 생성
- **데이터 모델** (MySQL):
  ```sql
  Salary:
  - salaryId (PK, auto_increment)
  - employeeId (FK → Employees)
  - payDate: date
  - baseSalary: decimal
  - allowance: decimal
  - bonus: decimal
  - totalPay: decimal (자동 계산)
  - incomeTax: decimal
  - residentTax: decimal
  - socialIns: decimal
  - totalDeductions: decimal (자동 계산)
  - netPay: decimal (자동 계산)
  ```

### 1.3 마이그레이션 목표

#### 기술 스택
- **프레임워크**: Spring Boot 3 (Java 21+)
- **ORM**: JPA (Hibernate) 또는 MyBatis
- **데이터베이스**:
  - MySQL (RDS): 사원, 급여, 학력, 경력, 자격증, 병역정보, 근태, 휴가, 공지사항 (DynamoDB 함수 변환)
- **인증**: JWT (bcrypt 해싱으로 보안 강화)
- **배포**: Docker + JAR 파일 (EC2)
- **보안**: Spring Security, Bcrypt, Validation API

#### 프로젝트 구조
```
BE/
├── src/
│   ├── main/
│   │   ├── java/com/yourcompany/erp/
│   │   │   ├── ErpApplication.java      # 메인 진입점
│   │   │   ├── config/                  # 설정 (Security, DB, CORS 등)
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── DatabaseConfig.java
│   │   │   │
│   │   │   ├── controller/              # HTTP 요청 처리 (API Router 역할)
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── EmployeeController.java
│   │   │   │   └── ... (기존 7개 기능별 Controller)
│   │   │   │
│   │   │   ├── service/                 # 핵심 비즈니스 로직 (CRUD 로직 포함)
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── EmployeeService.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── repository/              # DB 접근 (JPA/MyBatis)
│   │   │   │   ├── EmployeeRepository.java
│   │   │   │   ├── AttendanceRepository.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── domain/                  # JPA Entity (모델 역할)
│   │   │   │   ├── Employee.java
│   │   │   │   ├── Attendance.java
│   │   │   │   └── ... (기존 12개 Entity)
│   │   │   │
│   │   │   ├── dto/                     # DTO (Request/Response/Schema 역할)
│   │   │   │   ├── AuthDto.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   └── core/                    # Security, Util 등 공통 모듈
│   │   │
│   │   └── resources/
│   │       ├── application.yml          # 설정 파일
│   │       └── ...
│   │
│   └── test/                            # 테스트 코드
├── build.gradle (or pom.xml)
└── Dockerfile
```

## 2. 기능 요구사항

### 2.1 인증 및 권한 관리 API
- `POST /api/v1/auth/login`: 로그인
  - Request: `{ email, password }`
  - Response: `{ token, user: { employeeId, name, email, teamName, employmentType } }`
- `POST /api/v1/auth/change-password`: 비밀번호 변경 (JWT 필요)
  - Request: `{ currentPassword, newPassword }`
  - Response: `{ message }`
- `POST /api/v1/auth/logout`: 로그아웃 (선택사항, 토큰 블랙리스트)

### 2.2 사원 관리 API
- `POST /api/v1/employees`: 신규 사원 등록 (인사팀장만)
  - 기본정보, 병역정보, 급여정보, 학력, 경력, 자격증 일괄 등록
  - 트랜잭션 처리
- `GET /api/v1/employees`: 사원 목록 조회
  - 권한별 필터링 (인사팀장: 전체, 팀장: 부서 내, 일반: 불가)
  - Query: `name, employeeId, positionName, teamName`
- `GET /api/v1/employees/{employee_id}`: 사원 상세 조회
  - 본인: 전체 정보
  - 인사팀장: 타인 기본정보
  - 팀장: 같은 부서 전체 정보
- `PUT /api/v1/employees/{employee_id}`: 사원 정보 수정
  - 수정 가능 필드: phoneNumber, address, internalNumber, bankName, account

### 2.3 학력 관리 API
- `GET /api/v1/employees/{employee_id}/education`: 특정 직원 학력 조회 (본인 or 인사팀)
- `GET /api/v1/education`: 전체 학력 조회 (인사팀만)
  - Query: `name, employeeId, departmentName`
- `POST /api/v1/employees/{employee_id}/education`: 학력 등록 (인사팀만)
- `PATCH /api/v1/education/{education_id}`: 학력 수정 (인사팀만)
- `DELETE /api/v1/education/{education_id}`: 학력 삭제 (인사팀만)

### 2.4 근태 관리 API
- `POST /api/v1/attendance/clock-in`: 출근 기록
- `POST /api/v1/attendance/clock-out`: 퇴근 기록
- `GET /api/v1/attendance/records`: 근태 기록 조회
  - Query: `employeeId, yearMonth (YYYY-MM)`
- `PUT /api/v1/attendance/records`: 근태 기록 수정
- `DELETE /api/v1/attendance/records`: 근태 기록 삭제

### 2.5 휴가 관리 API
- `POST /api/v1/leave/requests`: 휴가 신청
- `GET /api/v1/leave/requests`: 휴가 신청 목록 조회
- `GET /api/v1/leave/requests/{request_id}`: 휴가 신청 상세 조회
- `PATCH /api/v1/leave/requests/{request_id}/status`: 휴가 승인/거절 (관리자)

### 2.6 공지사항 관리 API
- `POST /api/v1/posts`: 공지사항 생성
- `GET /api/v1/posts`: 공지사항 목록 조회 (페이지네이션)
- `GET /api/v1/posts/{post_id}`: 공지사항 상세 조회
- `PUT /api/v1/posts/{post_id}`: 공지사항 수정
- `DELETE /api/v1/posts/{post_id}`: 공지사항 삭제

### 2.7 급여 관리 API
- `POST /api/v1/salary/records`: 급여 정보 생성 (관리자)
- `GET /api/v1/salary/records`: 급여 정보 조회
  - Query: `employeeId, startDate, endDate`
- `GET /api/v1/salary/records/{record_id}`: 급여 상세 조회

## 3. 비기능 요구사항

### 3.1 성능
- API 응답 시간: 평균 200ms 이하
- 동시 접속: 최소 100명 지원
- Connection Pool: MySQL 10개, 효율적 재사용

### 3.2 보안
- **비밀번호**: Spring Security의 BcryptPasswordEncoder를 사용
- **JWT**:Spring Security를 통합하여 JWT 필터 및 토큰 검증 로직을 구현
- **입력값 검증**: Jakarta Bean Validation API (Hibernate Validator)를 사용하여 DTO 레벨에서 입력값 유효성을 검증
- **SQL Injection 방지**: JPA/Hibernate를 사용하여 SQL Injection을 방지
- **개인정보 보호**:
  - 주민등록번호(rrn) 암호화 고려
  - 비밀번호 응답에서 제외

### 3.3 권한 관리
- **일반 사원**: 본인 정보 조회/수정만
- **팀장** (positionLevel >= 6): 자기 부서 사원 조회
- **인사팀장** (인사팀 + Level >= 6): 전체 조회 및 등록/수정/삭제
- **권한 구현**: Spring Security의 @PreAuthorize 어노테이션 또는 URL 기반 설정으로 복잡한 권한 검증 로직을 구현합니다.
  - @PreAuthorize("hasRole('HR_MANAGER')")
  - @PreAuthorize("isAuthenticated() and @permissionChecker.isSelfOrManager(#employeeId)")와 같이 DI 기반 권한 검증 로직을 Spring Bean으로 구현

### 3.4 모니터링
- Health check 엔드포인트
- 구조화된 로깅 (SLF4J/Logback)
- 에러 추적 및 로그 분석

## 4. 데이터 마이그레이션 전략

### 4.1 데이터베이스 통합 (하이브리드)
- **MySQL**: 사원, 급여, 학력, 경력, 자격증, 병역정보, 근태, 휴가, 공지사항 
- **장점**:
  - 단일 데이터베이스 관리로 운영 복잡성 대폭 감소
  - 모든 데이터에 대해 SQL 트랜잭션 및 관계형 질의 (JOIN) 사용 가능
  - ORM (SQLAlchemy)만 사용하여 코드 단순화
- **단점**:
  - DynamoDB 데이터를 MySQL 스키마에 맞춰 마이그레이션하는 작업 필요

### 4.2 마이그레이션 단계
1. Spring Boot 서버 구축 (EC2 + Docker): EC2 인스턴스에 Spring Boot 환경을 구성하고 Docker 배포 준비.
2. MySQL 스키마 확장: 근태, 휴가, 공지사항을 위한 신규 테이블 생성.
3. 인증 시스템 마이그레이션: bcrypt 해싱 적용 및 JWT 구현.
4. 데이터 마이그레이션: 기존 DynamoDB 데이터를 MySQL 신규 테이블로 전환/이관하는 스크립트 작성 및 실행. 🐥
5. 기능별 순차 구현 및 테스트: 모든 기능을 JPA/Hibernate 기반 CRUD로 구현하고 테스트.
6. Lambda → Spring Boot 트래픽 전환 및 기존 Lambda/DynamoDB 제거.

## 5. 개발 원칙
1. **모듈화**: Spring Boot의 표준 컨트롤러(Controller)-서비스(Service)-레포지토리(Repository) 레이어를 따름
2. **타입 안전성**: Java의 강력한 타입 시스템을 활용
3. **에러 핸들링**: @ControllerAdvice를 사용하여 전역적인 예외 처리를 구현하고 적절한 HTTP 상태 코드를 반환
4. **보안 우선**: Spring Security를 중심으로 인증 및 인가 로직을 구현
5. **의존성 주입**: Spring의 Dependency Injection (DI) 컨테이너를 적극 활용하여 모듈 간 결합도를 낮춤
6. **트랜잭션**: @Transactional 어노테이션을 사용하여 비즈니스 로직의 트랜잭션을 관리
7. **테스트**: JUnit 5, Mockito를 사용한 단위 및 통합 테스트를 작성
8. **문서화**: OpenAPI/Swagger 자동 문서화

## 6. 일정 및 리소스

### 마일스톤
- **Phase 1-3**: 프로젝트 초기 설정 및 모델 구현 (2일)
- **Phase 4**: 인증 및 권한 시스템 구현 (1일)
- **Phase 5**: 사원 관리 API 구현 (2일)
- **Phase 6**: 학력 관리 API 구현 (1일)
- **Phase 7**: 근태 관리 API 구현 (1일)
- **Phase 8**: 휴가 관리 API 구현 (1일)
- **Phase 9**: 공지사항 관리 API 구현 (1일)
- **Phase 10**: 급여 관리 API 구현 (1일)
- **Phase 11-12**: 테스트 및 배포 준비 (2일)

**총 소요 기간: 약 12일**

## 7. 리스크 및 대응 방안

### 7.1 리스크
- 평문 비밀번호 → bcrypt 마이그레이션
- 복잡한 권한 관리 로직
- DynamoDB 데이터 MySQL 이관
- 기존 Lambda 함수와의 호환성

### 7.2 대응 방안
- 비밀번호 마이그레이션 스크립트 작성
- Dependency Injection 기반 권한 검증 함수
- 철저한 테스트 및 검증
- 점진적 마이그레이션 (기능별)
- 롤백 계획 수립
- 이관 스크립트의 무결성 테스트를 최우선으로 진행

## 8. 성공 지표
- 모든 기존 기능 정상 작동 (18개 핸들러 → 30+ 엔드포인트)
- API 응답 시간 200ms 이하
- 테스트 커버리지 80% 이상
- 보안 강화 (bcrypt 해싱 적용)
- 무중단 마이그레이션 완료

## 9. API 엔드포인트 요약

| 카테고리 | 메소드 | 경로 | 권한 |
|---------|--------|------|------|
| 인증 | POST | /api/v1/auth/login | Public |
| 인증 | POST | /api/v1/auth/change-password | Authenticated |
| 사원 | POST | /api/v1/employees | HR Manager |
| 사원 | GET | /api/v1/employees | Manager+ |
| 사원 | GET | /api/v1/employees/{id} | Self/Manager/HR |
| 사원 | PUT | /api/v1/employees/{id} | Self/Manager/HR |
| 학력 | GET | /api/v1/employees/{id}/education | Self/HR |
| 학력 | GET | /api/v1/education | HR Manager |
| 학력 | POST | /api/v1/employees/{id}/education | HR Manager |
| 학력 | PATCH | /api/v1/education/{id} | HR Manager |
| 학력 | DELETE | /api/v1/education/{id} | HR Manager |
| 근태 | POST | /api/v1/attendance/clock-in | Authenticated |
| 근태 | POST | /api/v1/attendance/clock-out | Authenticated |
| 근태 | GET | /api/v1/attendance/records | Self/Manager |
| 근태 | PUT | /api/v1/attendance/records | Manager/HR |
| 근태 | DELETE | /api/v1/attendance/records | Manager/HR |
| 휴가 | POST | /api/v1/leave/requests | Authenticated |
| 휴가 | GET | /api/v1/leave/requests | Self/Manager |
| 휴가 | PATCH | /api/v1/leave/requests/{id}/status | Manager |
| 공지 | POST | /api/v1/posts | Manager/HR |
| 공지 | GET | /api/v1/posts | Authenticated |
| 공지 | GET | /api/v1/posts/{id} | Authenticated |
| 공지 | PUT | /api/v1/posts/{id} | Author/HR |
| 공지 | DELETE | /api/v1/posts/{id} | Author/HR |
| 급여 | POST | /api/v1/salary/records | HR Manager |
| 급여 | GET | /api/v1/salary/records | Self/HR |
| 급여 | GET | /api/v1/salary/records/{id} | Self/HR |

**총 27개 주요 엔드포인트**
