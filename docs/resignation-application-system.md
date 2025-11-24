# 퇴직 신청 및 승인 시스템

## 📋 개요

직원이 퇴직을 신청하고, 관리자가 승인/반려를 처리하는 시스템입니다.
승인 시 자동으로 직원의 `quitDate`가 업데이트되며, 퇴직금 계산 API와 연동됩니다.

## 🏗️ 아키텍처

### 엔티티
- **ResignationApplication**: 퇴직 신청 엔티티
  - 신청자, 퇴직 희망일, 사유
  - 승인/반려 상태 및 처리 정보
  - 최종 퇴사일 (승인 시)

### 주요 기능
1. **퇴직 신청 생성**: 직원이 퇴직을 신청
2. **승인/반려 처리**: 관리자가 퇴직 신청을 승인/반려
3. **자동 업데이트**: 승인 시 Employee.quitDate 자동 업데이트
4. **신청 취소**: PENDING 상태의 신청만 취소 가능
5. **통계 조회**: 전체/대기/승인/반려 건수

## 📡 API 엔드포인트

### 1. 퇴직 신청 생성
```http
POST /api/resignations
Content-Type: application/json
Authorization: Bearer {token}

# 본인 신청 (employeeId 생략 - 토큰에서 자동 추출)
{
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유",
  "detailedReason": "가족 간병을 위한 퇴직"
}

# 관리자가 대신 신청 (employeeId 명시)
{
  "employeeId": 12345,
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유"
}
```

**응답 예시:**
```json
{
  "id": 1,
  "employee": {
    "id": 12345,
    "name": "홍길동",
    "email": "hong@example.com",
    "departmentName": "개발팀",
    "positionName": "과장",
    "hireDate": "2020-01-01"
  },
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유",
  "detailedReason": "가족 간병을 위한 퇴직",
  "status": "PENDING",
  "applicationDate": "2025-11-19T15:30:00",
  "processor": null,
  "processedAt": null,
  "rejectionReason": null,
  "finalResignationDate": null
}
```

### 2. 전체 퇴직 신청 조회 (페이징)
```http
GET /api/resignations?page=0&size=20&sort=applicationDate,desc
```

### 3. 퇴직 신청 상세 조회
```http
GET /api/resignations/{id}
```

### 4. 특정 직원의 퇴직 신청 조회
```http
GET /api/resignations/employee/{employeeId}
```

### 5. 상태별 퇴직 신청 조회
```http
GET /api/resignations/status/PENDING
GET /api/resignations/status/APPROVED
GET /api/resignations/status/REJECTED
```

### 6. 퇴직 신청 승인
```http
PUT /api/resignations/{id}/process
Content-Type: application/json
Authorization: Bearer {token}

# processorId 생략 가능 (토큰에서 자동 추출)
{
  "approved": true,
  "finalResignationDate": "2025-12-31"
}
```

**승인 시 동작:**
1. ResignationApplication.status → APPROVED
2. ResignationApplication.finalResignationDate 설정
3. **Employee.quitDate 자동 업데이트** ✅
4. 퇴직금 계산 API에서 자동 반영

### 7. 퇴직 신청 반려
```http
PUT /api/resignations/{id}/process
Content-Type: application/json
Authorization: Bearer {token}

# processorId 생략 가능 (토큰에서 자동 추출)
{
  "approved": false,
  "rejectionReason": "퇴직 시기 조정 필요"
}
```

### 8. 퇴직 신청 취소 (신청자만)
```http
PUT /api/resignations/{id}/cancel
```

### 9. 퇴직 신청 삭제 (관리자만)
```http
DELETE /api/resignations/{id}
```

### 10. 퇴직 신청 통계
```http
GET /api/resignations/statistics
```

**응답 예시:**
```json
{
  "totalApplications": 100,
  "pendingApplications": 10,
  "approvedApplications": 80,
  "rejectedApplications": 10
}
```

## 🔄 워크플로우

### 1. 직원 퇴직 신청
```
직원 → POST /api/resignations
     → ResignationApplication 생성 (status: PENDING)
```

### 2. 관리자 승인
```
관리자 → PUT /api/resignations/{id}/process (approved: true)
      → ResignationApplication.status = APPROVED
      → ResignationApplication.finalResignationDate 설정
      → Employee.quitDate = finalResignationDate ✅
      → 퇴직금 계산 가능
```

### 3. 관리자 반려
```
관리자 → PUT /api/resignations/{id}/process (approved: false)
      → ResignationApplication.status = REJECTED
      → ResignationApplication.rejectionReason 저장
      → Employee.quitDate 변경 없음
```

### 4. 신청자 취소
```
신청자 → PUT /api/resignations/{id}/cancel
      → ResignationApplication.status = REJECTED
      → rejectionReason = "신청자가 취소함"
```

## 🔗 연동 시스템

### 1. 퇴직금 계산 API
퇴직 신청이 승인되면 `Employee.quitDate`가 자동으로 설정되므로,
퇴직금 계산 API에서 바로 조회 가능:

```http
# 개별 퇴직금 계산
GET /api/severance/calculate?employeeId=12345&severanceDate=2025-12-31

# 퇴직자 목록 조회 (2025년)
GET /api/severance/retirements?year=2025
```

### 2. 직원 정보 조회
```http
GET /api/employees/{id}
```

퇴직자는 `quitDate` 필드가 있으므로 필터링 가능.

## 🗃️ 데이터베이스

### resignation_applications 테이블
```sql
CREATE TABLE resignation_applications (
    resignationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    desiredResignationDate DATE NOT NULL,
    reason TEXT NOT NULL,
    detailedReason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applicationDate DATETIME NOT NULL,
    processedBy BIGINT,
    processedAt DATETIME,
    rejectionReason VARCHAR(500),
    finalResignationDate DATE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (processedBy) REFERENCES employees(employeeId)
);
```

### 마이그레이션
- **V5.0__create_resignation_application_table.sql**
  - resignation_applications 테이블 생성
  - 인덱스: employeeId, status, applicationDate, desiredResignationDate

## ✅ 검증 규칙

1. **신청 시**:
   - 이미 퇴사 처리된 직원은 신청 불가
   - 대기 중인 퇴직 신청이 있으면 중복 신청 불가

2. **승인/반려 시**:
   - PENDING 상태만 처리 가능
   - 반려 시 반려 사유 필수

3. **취소 시**:
   - PENDING 상태만 취소 가능

## 🧪 테스트 시나리오

### 1. 정상 케이스
```bash
# 1. 퇴직 신청 생성 (employeeId 생략 - 토큰에서 자동)
POST /api/resignations
Authorization: Bearer {token}
{
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유"
}

# 2. 승인 처리 (processorId 생략 - 토큰에서 자동)
PUT /api/resignations/1/process
Authorization: Bearer {token}
{
  "approved": true,
  "finalResignationDate": "2025-12-31"
}

# 3. Employee.quitDate 확인
GET /api/employees/12345
# quitDate: "2025-12-31" ✅

# 4. 퇴직금 계산
GET /api/severance/calculate?employeeId=12345&severanceDate=2025-12-31
```

### 2. 반려 케이스
```bash
# 1. 퇴직 신청 생성
POST /api/resignations
{
  "employeeId": 12345,
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유"
}

# 2. 반려 처리
PUT /api/resignations/1/process
{
  "processorId": 111111,
  "approved": false,
  "rejectionReason": "퇴직 시기 조정 필요"
}

# 3. Employee.quitDate 확인
GET /api/employees/12345
# quitDate: null ✅ (변경 없음)
```

### 3. 취소 케이스
```bash
# 1. 퇴직 신청 생성
POST /api/resignations
{
  "employeeId": 12345,
  "desiredResignationDate": "2025-12-31",
  "reason": "개인 사유"
}

# 2. 신청자가 취소
PUT /api/resignations/1/cancel

# 3. 상태 확인
GET /api/resignations/1
# status: "REJECTED"
# rejectionReason: "신청자가 취소함"
```

## 📊 통계 및 모니터링

```bash
# 전체 통계
GET /api/resignations/statistics

# 대기 중인 신청 조회
GET /api/resignations/status/PENDING

# 승인된 신청 조회
GET /api/resignations/status/APPROVED

# 특정 직원의 퇴직 신청 이력
GET /api/resignations/employee/12345
```

## 🚀 배포 시 체크리스트

1. ✅ V5.0 마이그레이션 파일 확인
2. ✅ resignation_applications 테이블 생성
3. ✅ Employee.updateQuitDate() 메서드 추가
4. ✅ ResignationApplication 엔티티 생성
5. ✅ ResignationApplicationRepository 생성
6. ✅ ResignationApplicationService 생성
7. ✅ ResignationApplicationController 생성
8. ✅ ResignationApplicationDto 생성
9. ✅ API 테스트
10. ✅ 퇴직금 계산 연동 테스트

## 📝 참고사항

- **권한 관리**: 추가 구현 시 Spring Security로 권한 체크 필요
  - 신청: 본인만
  - 승인/반려: 관리자만
  - 취소: 본인만
  - 삭제: 관리자만

- **알림**: 승인/반려 시 직원에게 알림 기능 추가 가능

- **이력 관리**: BaseEntity 상속으로 createdAt, updatedAt 자동 관리

- **N+1 문제 해결**: JOIN FETCH를 통해 성능 최적화
