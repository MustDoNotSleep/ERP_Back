# 급여 필터링 일괄 수정 API

## 개요
프론트엔드에서 요청한 **필터링된 급여 일괄 수정 기능**입니다.  
특정 조건(전체/부서/직급/개인)에 해당하는 직원들의 급여를 한 번에 수정할 수 있습니다.

## 🚀 API 목록

### 1️⃣ 필터링된 급여 일괄 수정 (핵심)

**Endpoint:** `PUT /salary/bulk-update-filtered`

**권한:** `ADMIN`, `HR_MANAGER`

**Request Body:**
```json
{
  "paymentDate": "2025-01",           // 대상 월 (필수)
  "targetType": "DEPARTMENT",          // 대상 유형 (필수): ALL, DEPARTMENT, POSITION, EMPLOYEE
  "targetDepartment": "개발팀",        // 부서별일 경우 (targetType=DEPARTMENT 시 필수)
  "targetPosition": "대리",            // 직급별일 경우 (targetType=POSITION 시 필수)
  "targetEmployeeId": 123,             // 개인별일 경우 (targetType=EMPLOYEE 시 필수)
  
  // 추가할 금액 (null이면 해당 항목 수정 안 함)
  "bonusToAdd": 100000,                // 보너스 추가
  "overtimeAllowanceToAdd": 50000,     // 야근수당 추가
  "nightAllowanceToAdd": 30000         // 야간수당 추가
}
```

**Response:**
```json
{
  "updatedCount": 12,                  // 수정된 급여 건수
  "message": "개발팀 12명의 급여가 수정되었습니다.",
  "paymentDate": "2025-01",
  "targetType": "DEPARTMENT",
  "targetInfo": "개발팀"                // 대상 정보
}
```

### 2️⃣ 월별 급여 목록 조회 ✅

**Endpoint:** `GET /salary/month/{yearMonth}`

**권한:** `ADMIN`, `HR_MANAGER`

**Example:** `GET /salary/month/2025-01`

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 12341,
    "employeeName": "김철수",
    "departmentName": "개발팀",
    "positionName": "대리",
    "paymentDate": "2025-01",
    "baseSalary": 3500000,
    "bonus": 100000,
    "totalSalary": 3600000,
    "netSalary": 3100000,
    "salaryStatus": "DRAFT"
  }
]
```

### 3️⃣ 급여 개별 수정 ✅

**Endpoint:** `PUT /salary/{id}`

**권한:** `ADMIN`, `HR_MANAGER`

**Request Body:**
```json
{
  "bonus": 500000,
  "overtimeAllowance": 100000
}
```

### 4️⃣ 급여 삭제 ⭐ (신규)

**Endpoint:** `DELETE /salary/{id}`

**권한:** `ADMIN`, `HR_MANAGER`

**Response:** `204 No Content`

**제약사항:**
- `PAID` 상태인 급여는 삭제 불가
- `DRAFT`, `CONFIRMED` 상태만 삭제 가능

**Error Response:**
```json
{
  "message": "이미 지급 완료된 급여는 삭제할 수 없습니다."
}
```

## 📋 대상 유형 (UpdateTargetType)

| 값 | 설명 | 필수 필드 |
|----|------|----------|
| `ALL` | 전체 직원 | 없음 |
| `DEPARTMENT` | 특정 부서 | `targetDepartment` |
| `POSITION` | 특정 직급 | `targetPosition` |
| `EMPLOYEE` | 특정 직원 | `targetEmployeeId` |

## 🎯 사용 예시

### 예시 1: 개발팀 전체에 보너스 지급

```http
PUT /salary/bulk-update-filtered
```

```json
{
  "paymentDate": "2025-01",
  "targetType": "DEPARTMENT",
  "targetDepartment": "개발팀",
  "bonusToAdd": 500000
}
```

→ 개발팀 소속 직원들의 2025년 1월 급여에 보너스 50만원 추가

### 예시 2: 대리 직급 전체에 야근수당 지급

```http
PUT /salary/bulk-update-filtered
```

```json
{
  "paymentDate": "2025-01",
  "targetType": "POSITION",
  "targetPosition": "대리",
  "overtimeAllowanceToAdd": 100000
}
```

→ 대리 직급 직원들의 2025년 1월 급여에 야근수당 10만원 추가

### 예시 3: 특정 직원에게 야간수당 지급

```http
PUT /salary/bulk-update-filtered
```

```json
{
  "paymentDate": "2025-01",
  "targetType": "EMPLOYEE",
  "targetEmployeeId": 12341,
  "nightAllowanceToAdd": 50000
}
```

→ 직원 ID 12341의 2025년 1월 급여에 야간수당 5만원 추가

### 예시 4: 전체 직원에게 설 보너스 지급

```http
PUT /salary/bulk-update-filtered
```

```json
{
  "paymentDate": "2025-02",
  "targetType": "ALL",
  "bonusToAdd": 1000000
}
```

→ 전체 직원의 2025년 2월 급여에 보너스 100만원 추가

## ⚙️ 동작 방식

1. **대상 필터링**
   - `targetType`에 따라 해당 월의 급여 레코드 조회
   - Repository 쿼리로 Employee, Department, Position JOIN

2. **금액 추가**
   - `bonusToAdd` 등의 값을 **기존 값에 더함** (덮어쓰기 아님)
   - `null`이 아닌 항목만 처리

3. **자동 재계산**
   - `calculateTotal()`: 총 급여 재계산
   - `calculateNetSalary()`: 세금/보험 자동 재계산 후 실수령액 계산

4. **결과 반환**
   - 수정된 건수와 대상 정보 응답

## 🔒 권한 및 제약사항

### 권한
- `ADMIN`: 모든 API 접근 가능
- `HR_MANAGER`: 모든 API 접근 가능
- `USER`: 조회만 가능 (자신의 급여만)

### 제약사항
1. **삭제 제한**
   - `PAID` 상태 급여는 삭제 불가
   - 회계 기록 보존을 위함

2. **필터링 조건**
   - `targetType`에 맞는 필수 필드 누락 시 에러
   - 존재하지 않는 부서/직급/직원 ID 입력 시 빈 결과 반환

3. **금액 처리**
   - 음수 금액도 가능 (급여 차감)
   - `0` 입력 시 변경 없음 (처리 생략)

## 📊 데이터베이스 쿼리

### 전체 조회
```sql
SELECT s.* FROM salary s
WHERE s.payment_date = '2025-01';
```

### 부서별 조회
```sql
SELECT s.* FROM salary s
JOIN employee e ON s.employee_id = e.id
JOIN department d ON e.department_id = d.id
WHERE s.payment_date = '2025-01' AND d.department_name = '개발팀';
```

### 직급별 조회
```sql
SELECT s.* FROM salary s
JOIN employee e ON s.employee_id = e.id
JOIN position p ON e.position_id = p.id
WHERE s.payment_date = '2025-01' AND p.position_name = '대리';
```

### 개인 조회
```sql
SELECT s.* FROM salary s
WHERE s.payment_date = '2025-01' AND s.employee_id = 12341;
```

## 🧪 테스트 시나리오

### 1. 정상 케이스
- [x] 전체 직원 보너스 일괄 지급
- [x] 개발팀만 보너스 지급
- [x] 대리 직급만 야근수당 지급
- [x] 특정 직원에게 야간수당 지급

### 2. 예외 케이스
- [x] 존재하지 않는 부서명 입력
- [x] 존재하지 않는 직급명 입력
- [x] 존재하지 않는 직원 ID 입력
- [x] 필수 필드 누락 (400 Bad Request)
- [x] PAID 상태 급여 삭제 시도 (400 Bad Request)

### 3. 재계산 검증
- [x] 보너스 추가 후 총 급여 자동 증가
- [x] 총 급여 증가 후 세금/보험 자동 재계산
- [x] 실수령액 정확성 검증

## 🔄 기존 API와의 차이점

### 기존: `/salary/bulk-update`
- **대상**: 특정 월 전체 직원 (필터링 불가)
- **용도**: 명절 보너스 등 전사 일괄 지급

### 신규: `/salary/bulk-update-filtered` ⭐
- **대상**: 조건별 필터링 가능 (부서/직급/개인)
- **용도**: 부서별 인센티브, 직급별 수당 등 세밀한 제어

## 🎉 프론트엔드 통합 가이드

### 필터 UI 구성
```tsx
<Select value={targetType} onChange={setTargetType}>
  <option value="ALL">전체 직원</option>
  <option value="DEPARTMENT">부서별</option>
  <option value="POSITION">직급별</option>
  <option value="EMPLOYEE">개인별</option>
</Select>

{targetType === 'DEPARTMENT' && (
  <Input 
    placeholder="부서명 입력" 
    value={targetDepartment}
    onChange={setTargetDepartment}
  />
)}

{targetType === 'POSITION' && (
  <Input 
    placeholder="직급명 입력" 
    value={targetPosition}
    onChange={setTargetPosition}
  />
)}

{targetType === 'EMPLOYEE' && (
  <Input 
    type="number"
    placeholder="직원 ID" 
    value={targetEmployeeId}
    onChange={setTargetEmployeeId}
  />
)}
```

### API 호출 예시
```typescript
const response = await fetch('/salary/bulk-update-filtered', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    paymentDate: '2025-01',
    targetType: 'DEPARTMENT',
    targetDepartment: '개발팀',
    bonusToAdd: 500000
  })
});

const result = await response.json();
alert(`${result.message}`); // "개발팀 12명의 급여가 수정되었습니다."
```

## 📝 추가 개선 사항 (향후)

- [ ] 여러 부서/직급 동시 선택 (배열 입력)
- [ ] 급여 일괄 생성 API (급여 미생성 직원 자동 생성)
- [ ] 수정 이력 로깅 (누가 언제 얼마를 수정했는지)
- [ ] Dry-run 모드 (실제 저장 전 미리보기)
- [ ] Excel 업로드를 통한 대량 수정
