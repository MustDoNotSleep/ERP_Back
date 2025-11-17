# 급여 세금 자동 계산 시스템

## 개요
2024년 기준 **간이세액표** 및 **4대보험 법정 요율**을 적용한 자동 계산 시스템입니다.

## 적용된 세금 및 보험 요율

### 1. 소득세
- **간이세액표 기준** (부양가족 1명, 80% 세율)
- 월 급여 구간별 누진세율 적용
- 10원 단위 절사

| 월 급여 구간 | 세율 |
|------------|------|
| 106만원 미만 | 비과세 |
| 106만원 ~ 150만원 | 4% |
| 150만원 ~ 200만원 | 5% |
| 200만원 ~ 250만원 | 7% |
| 250만원 ~ 300만원 | 8% |
| 300만원 ~ 450만원 | 12% |
| 450만원 ~ 600만원 | 15% |
| 600만원 ~ 880만원 | 24% |
| 880만원 ~ 1500만원 | 35% |
| 1500만원 이상 | 38% |

### 2. 지방소득세
- **소득세의 10%** (자동 계산)
- 10원 단위 절사

### 3. 4대보험 (2024년 기준)

#### 국민연금
- **요율**: 4.5%
- **기준소득월액 범위**: 370,000원 ~ 5,900,000원
- 10원 단위 절사

#### 건강보험
- **요율**: 3.545%
- **장기요양보험**: 건강보험료의 12.95% (0.4591%)
- **기준소득월액 상한**: 8,653,000원
- 10원 단위 절사

#### 고용보험
- **요율**: 0.9%
- 10원 단위 절사

## API 사용 방법

### 1. 급여 생성 (자동 계산)

세금 및 4대보험을 **생략**하면 자동으로 계산됩니다.

```http
POST /salary
```

**Request Body (자동 계산 사용):**
```json
{
  "employeeId": 12341,
  "paymentDate": "2024-11",
  "baseSalary": 3500000,
  "overtimeAllowance": 150000,
  "nightAllowance": 0,
  "bonus": 0
}
```

**Response:**
```json
{
  "id": 1,
  "employeeId": 12341,
  "employeeName": "김철수",
  "paymentDate": "2024-11",
  "baseSalary": 3500000,
  "overtimeAllowance": 150000,
  "nightAllowance": 0,
  "bonus": 0,
  "totalSalary": 3650000,
  "incomeTax": 169600,        // 자동 계산됨
  "localIncomeTax": 16960,    // 자동 계산됨 (소득세의 10%)
  "nationalPension": 164250,  // 자동 계산됨 (4.5%)
  "healthInsurance": 141300,  // 자동 계산됨 (3.545% + 장기요양)
  "employmentInsurance": 32850, // 자동 계산됨 (0.9%)
  "otherDeductions": 0,
  "netSalary": 3124040,       // 실수령액
  "salaryStatus": "DRAFT"
}
```

### 2. 급여 생성 (수동 입력)

세금 및 4대보험을 **직접 입력**할 수도 있습니다.

```http
POST /salary
```

**Request Body (수동 입력):**
```json
{
  "employeeId": 12341,
  "paymentDate": "2024-11",
  "baseSalary": 3500000,
  "overtimeAllowance": 150000,
  "incomeTax": 200000,          // 수동 입력
  "nationalPension": 170000,    // 수동 입력
  "healthInsurance": 150000,    // 수동 입력
  "employmentInsurance": 35000  // 수동 입력
}
```

### 3. 세금 계산 미리보기

급여액을 입력하면 **세금과 보험료를 미리 계산**해볼 수 있습니다.

```http
GET /salary/calculate-preview?totalSalary=3650000
```

**Response:**
```json
{
  "totalSalary": 3650000,
  "incomeTax": 169600,
  "localIncomeTax": 16960,
  "nationalPension": 164250,
  "healthInsurance": 141300,
  "employmentInsurance": 32850,
  "totalDeductions": 524960,
  "netSalary": 3125040,
  "deductionRate": "14.38%"
}
```

### 4. 급여 수정

급여 수정 시에도 자동 재계산이 적용됩니다.

```http
PUT /salary/{id}
```

**Request Body:**
```json
{
  "bonus": 1000000  // 보너스만 추가
}
```

→ 보너스 추가 후 총 급여가 변경되면 세금/보험이 **자동으로 재계산**됩니다.

### 5. 일괄 보너스 지급

특정 월 전체 직원에게 일괄 보너스 지급 시 자동 재계산됩니다.

```http
PUT /salary/bulk-update
```

**Request Body:**
```json
{
  "paymentDate": "2024-11",
  "bonusToAdd": 500000
}
```

→ 모든 직원의 보너스에 50만원 추가 후 세금/보험 **자동 재계산**

## 계산 흐름

```
1. 기본급 + 수당 + 보너스 = 총 급여 (calculateTotal)
   ↓
2. 세금 및 4대보험 계산 (calculateTaxAndInsurance)
   - Request에 값이 있으면 → 그 값 사용
   - Request에 값이 없으면 → 자동 계산
   ↓
3. 실수령액 계산 (calculateNetSalary)
   - 총 급여 - 모든 공제액 = 실수령액
```

## 계산 예시

### 월급 350만원 + 야근수당 15만원 = 총 365만원

| 항목 | 금액 | 계산식 |
|-----|------|--------|
| 기본급 | 3,500,000원 | |
| 야근수당 | 150,000원 | |
| **총 급여** | **3,650,000원** | |
| | | |
| 소득세 | 169,600원 | 간이세액표 적용 |
| 지방소득세 | 16,960원 | 소득세 × 10% |
| 국민연금 | 164,250원 | 총 급여 × 4.5% |
| 건강보험 | 141,300원 | 총 급여 × (3.545% + 0.4591%) |
| 고용보험 | 32,850원 | 총 급여 × 0.9% |
| **총 공제액** | **524,960원** | 약 14.38% |
| | | |
| **실수령액** | **3,125,040원** | 총 급여 - 총 공제액 |

## 코드 구조

```
src/main/java/com/erp/
├── util/
│   └── TaxCalculator.java          # 세금/보험 계산 유틸리티
├── entity/
│   └── Salary.java                 # calculateTaxAndInsurance() 메서드
├── service/
│   └── SalaryService.java          # 자동 계산 적용
└── controller/
    └── SalaryController.java       # /calculate-preview API
```

## 주의사항

1. **부양가족 수 고정**: 현재는 부양가족 1명 기준으로 계산됩니다.
2. **연말정산 미포함**: 간이세액표 기준이므로 실제 연말정산과 차이가 있을 수 있습니다.
3. **요율 변경**: 법정 요율이 변경되면 `TaxCalculator.java`를 수정해야 합니다.
4. **수동 입력 우선**: Request에 세금/보험 값을 포함하면 자동 계산을 무시하고 해당 값을 사용합니다.

## 향후 개선 사항

- [ ] 부양가족 수에 따른 세액 조정
- [ ] 중도 입퇴사자 일할 계산
- [ ] 비과세 항목 분리 (식대, 차량유지비 등)
- [ ] 연말정산 환급/추징 처리
- [ ] 세율 변경 이력 관리
