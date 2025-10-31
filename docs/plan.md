# Spring Boot 마이그레이션 구현 계획

## 일일 구현 계획

### Day 1 (Phase 1-3): 초기 설정 및 기본 인프라
- [x] Phase 1: 프로젝트 초기화 및 기본 설정
  - Spring Boot 프로젝트 생성
  - 디렉토리 구조 설정
  - 의존성 구성
- [x] Phase 2: 기본 설정 파일 구성
  - SecurityConfig, JpaConfig, WebConfig
  - 환경 설정 (application.yml)
- [x] Phase 3: 데이터베이스 연결 설정
  - MySQL 연결 설정
  - 커넥션 풀 구성

### Day 2 (Phase 4-5): 엔티티 및 스키마 설계
- [x] Phase 4: JPA 엔티티 설계
  - Employee, Department, Position 등 12개 엔티티 완료
  - Entity 관계 설정 완료
  - Enum 분리 (14개 독립 파일)
- [x] Phase 5: Flyway 마이그레이션 스크립트 작성
  - 초기 스키마 생성 스크립트 (V1__init.sql 등)
  - MySQL 테이블 구조 완성

### Day 3 (Phase 6): 보안 및 인증 구현
- [x] Phase 6: Spring Security + JWT 구현
  - SecurityConfig.java 완성 (CORS, 권한별 엔드포인트 설정)
  - JwtService.java 완성 (토큰 생성/검증)
  - JwtAuthenticationFilter.java 완성
  - AuthenticationController.java 완성 (로그인 API)
  - AuthenticationService.java 완성
  - AuthenticationRequest/Response DTO 완성
  - Employee 엔티티 UserDetails 구현 (Position 기반 Role 시스템)

### Day 4-5 (Phase 7): Repository 및 기본 CRUD
- [x] Phase 7: Repository 및 Service 레이어
  - 12개 Repository 완성 (Long ID로 통일)
  - EmployeeService, DepartmentService 완성
  - EmployeeController, DepartmentController 완성
  - DTO 12개 생성/수정 완료

### 다음 단계: Day 6-10 (Phase 8-12)
- [ ] Phase 8: Education 모듈 완성
  - EducationService, EducationController 보완
  - WorkExperienceService, WorkExperienceController 보완
  - CertificateService, CertificateController 보완
  
- [ ] Phase 9: Attendance 모듈 (주석 해제 및 구현)
  - Attendance 엔티티 활성화
  - AttendanceService 구현
  - AttendanceController 구현
  - 출퇴근 기록 API

- [ ] Phase 10: Leave 모듈 (주석 해제 및 구현)
  - Leave 엔티티 활성화
  - LeaveService 구현
  - LeaveController 구현
  - 휴가 신청/승인 워크플로우

- [ ] Phase 11: Post 모듈 (주석 해제 및 구현)
  - Post 엔티티 활성화
  - PostService 구현
  - PostController 구현
  - 공지사항 CRUD

- [ ] Phase 12: Salary 모듈 (주석 해제 및 구현)
  - Salary 엔티티 활성화
  - SalaryService 구현
  - SalaryController 구현
  - 급여 정보 관리

[이하 각 Day별 상세 계획]

## 1. 세부 구현 계획 및 TODO 리스트

### 1.1 프로젝트 초기화
```bash
# Spring Boot 3.2+ 프로젝트 생성
spring init \
  --boot-version=3.2 \
  --java-version=21 \
  --dependencies=web,data-jpa,security,validation,mysql \
  --groupId=com.yourcompany \
  --artifactId=erp \
  --name=erp \
  --description="ERP System" \
  --package-name=com.yourcompany.erp \
  --build=gradle \
  erp
```

### 1.2 기본 디렉토리 구조
```
src/main/java/com/yourcompany/erp/
├── ErpApplication.java
├── config/
│   ├── SecurityConfig.java        # Spring Security 설정
│   ├── JpaConfig.java            # JPA/Hibernate 설정
│   ├── WebConfig.java            # CORS, WebMVC 설정
│   └── SwaggerConfig.java        # API 문서화
├── domain/                        # JPA 엔티티
│   ├── employee/
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   └── Position.java
│   ├── education/
│   │   └── Education.java
│   └── ...
├── dto/                          # Request/Response DTO
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   └── TokenResponse.java
│   └── ...
├── repository/                    # JPA Repository
├── service/                      # 비즈니스 로직
└── controller/                   # REST API
```

### 1.3 주요 의존성 (build.gradle)
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'
    
    runtimeOnly 'com.mysql:mysql-connector-j'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

## 2. 데이터베이스 스키마 마이그레이션 (D+2)

### 2.1 JPA 엔티티 설계
- \`Employee\`, \`Department\`, \`Position\` 등 12개 엔티티 클래스 구현
- DynamoDB → MySQL 전환을 위한 새로운 테이블 설계:
  - Attendance (근태)
  - Leave (휴가)
  - Post (공지사항)

### 2.2 Flyway 마이그레이션 스크립트
```sql
-- V1__init_schema.sql
CREATE TABLE employees (
    employee_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,  -- BCrypt
    -- 기타 필드
);

-- V2__create_attendance.sql
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    clock_in TIME,
    clock_out TIME,
    type VARCHAR(20),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- 추가 마이그레이션 스크립트
```

### 2.3 데이터 마이그레이션 도구
- DynamoDB → MySQL ETL 스크립트 작성
- 비밀번호 BCrypt 변환 스크립트 구현

## 3. 보안 및 인증 구현 (D+3)

### 3.1 Spring Security 설정
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/employees/**").hasAnyRole("HR_MANAGER", "MANAGER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### 3.2 JWT 구현
- 토큰 생성, 검증, 파싱 유틸리티 클래스
- Authentication 필터 구현
- 권한 검증 어노테이션 구현

## 4. 핵심 기능 구현 (D+4 ~ D+10)

### 4.1 Employee 모듈 (D+4~5)
- 엔티티, DTO, 서비스, 컨트롤러 구현
- 트랜잭션 처리 및 권한 검증
- 부서별 접근 제어 구현

### 4.2 Education 모듈 (D+6)
- 학력 관리 CRUD
- 권한 기반 접근 제어

### 4.3 Attendance 모듈 (D+7)
```java
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @PostMapping("/clock-in")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clockIn(@AuthenticationPrincipal UserDetails user) {
        // 출근 기록 생성
    }
    
    @GetMapping("/records")
    @PreAuthorize("@permissionChecker.canAccessAttendance(#employeeId)")
    public Page<AttendanceResponse> getRecords(
        @RequestParam String employeeId,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
        Pageable pageable
    ) {
        // 근태 기록 조회
    }
}
```

### 4.4 Leave 모듈 (D+8)
- 휴가 신청/승인 워크플로우
- 이메일 알림 (옵션)

### 4.5 Post 모듈 (D+9)
- 공지사항 CRUD
- 첨부파일 처리 (AWS S3 연동)

### 4.6 Salary 모듈 (D+10)
- 급여 정보 관리
- PDF 출력 기능 (옵션)

## 5. 테스트 및 품질 관리 (D+11)

### 5.1 단위 테스트
```java
@SpringBootTest
class EmployeeServiceTest {
    @Test
    void createEmployee_WithValidData_ShouldCreateEmployee() {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest(...);
        
        // When
        EmployeeResponse response = employeeService.createEmployee(request);
        
        // Then
        assertNotNull(response.getEmployeeId());
        assertEquals(request.getName(), response.getName());
    }
}
```

### 5.2 통합 테스트
- API 엔드포인트 테스트
- 권한 검증 테스트
- 트랜잭션 테스트

## 6. 배포 및 운영 (D+12)

### 6.1 Docker 배포
```dockerfile
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/erp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.2 데이터베이스 마이그레이션
1. 스테이징 환경에서 마이그레이션 테스트
2. MySQL RDS 준비
3. ETL 스크립트 실행
4. 데이터 검증

### 6.3 모니터링 설정
- Actuator 엔드포인트 활성화
- 로깅 설정 (Logback)
- 메트릭 수집 (Prometheus/Grafana)

## 7. 롤백 계획

### 7.1 데이터베이스 롤백
- 마이그레이션 전 MySQL 스냅샷
- DynamoDB 백업 유지

### 7.2 애플리케이션 롤백
- 이전 Lambda 함수 유지
- DNS 기반 트래픽 전환

## 8. 성능 최적화

### 8.1 캐시 전략
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("employees", "departments");
    }
}
```

### 8.2 데이터베이스 최적화
- 인덱스 전략
- Connection Pool 설정
- Query 최적화

## 9. 문서화

### 9.1 API 문서
- Swagger UI (/swagger-ui.html)
- README.md 업데이트

### 9.2 운영 문서
- 배포 가이드
- 트러블슈팅 가이드
- 모니터링 가이드

## Phase별 TODO 리스트 요약

### Phase 1: 프로젝트 초기화 및 기본 설정
- [x] 1.1 Spring Boot 프로젝트 생성
  ```bash
  spring init \
    --boot-version=3.2 \
    --java-version=21 \
    --dependencies=web,data-jpa,security,validation,mysql \
    --groupId=com.yourcompany \
    --artifactId=erp \
    --name=erp \
    --description="ERP System" \
    --package-name=com.yourcompany.erp \
    --build=gradle \
    erp
  ```
- [x] 1.2 디렉토리 구조 설정
- [x] 1.3 의존성 추가 (build.gradle)

[이하 각 Phase별 상세 TODO 리스트]

## 개발 원칙
1. **모듈화**: 표준 Spring Boot 계층 구조 준수
2. **타입 안전성**: Java의 타입 시스템 활용
3. **에러 처리**: @ControllerAdvice로 일관된 예외 처리
4. **보안**: Spring Security 기반 인증/인가
5. **의존성 주입**: 생성자 주입 방식 사용
6. **트랜잭션**: @Transactional 적절히 활용
7. **테스트**: 단위/통합 테스트 필수
8. **문서화**: Swagger/OpenAPI 활용

## 일정 관리
각 Phase는 정해진 일정(Day)에 맞춰 진행하며, 다음 단계로 넘어가기 전에 반드시 현재 Phase의 모든 항목이 완료되어야 합니다.

### 작업 요청 방법
특정 Phase를 진행하려면 다음과 같이 요청하세요:
1. "Phase 1 진행해줘"
2. "1.1 작업 진행해줘"
3. "Day 1 작업 시작하자"

### 체크리스트
매일 작업 시작 전:
- [ ] 전날 작업 리뷰
- [ ] 당일 목표 Phase 확인
- [ ] 필요한 리소스 준비

매일 작업 종료 전:
- [ ] 완료된 작업 체크
- [ ] 커밋 및 푸시
- [ ] 다음 날 작업 준비

## 긴급상황 대응 계획
1. 롤백 계획 준비
2. 백업 및 복구 절차 문서화
3. 비상연락망 구성