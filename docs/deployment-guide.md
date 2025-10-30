# ERP 시스템 배포 가이드

## 1. 시스템 아키텍처

### 1.1 구성요소
- Application Server: EC2 (Docker 컨테이너)
- Database Server: RDS (MySQL)
- Storage: EBS (EC2 인스턴스 스토리지)

### 1.2 네트워크 구성
- VPC: AWS 기본 VPC 사용
- Subnet: 퍼블릭 서브넷 (EC2용)
- Security Groups:
  - EC2 Security Group
  - RDS Security Group

## 2. 사전 준비사항

### 2.1 AWS 설정
1. EC2 인스턴스 생성
   - AMI: Amazon Linux 2
   - 인스턴스 유형: t3.small
   - 스토리지: 20GB gp3
   - Security Group: 
     - SSH (22)
     - HTTP (80)
     - HTTPS (443)
     - Application (8080)

2. RDS 인스턴스 설정
   - Engine: MySQL 8.0
   - 인스턴스 클래스: db.t3.micro
   - 스토리지: 20GB gp3
   - Multi-AZ: No (비용 절감)

### 2.2 도메인 및 SSL
- Route 53 도메인 설정 (선택사항)
- ACM 인증서 발급 (선택사항)

## 3. 애플리케이션 배포

### 3.1 Docker 설정
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.2 환경 변수 설정
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://${RDS_ENDPOINT}:3306/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

### 3.3 EC2 초기 설정
```bash
# 시스템 업데이트
sudo yum update -y

# Docker 설치
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3.4 배포 스크립트
```bash
#!/bin/bash

# deploy.sh
DOCKER_IMAGE="your-image:latest"
RDS_ENDPOINT="your-rds-endpoint"
DB_NAME="erp"
DB_USERNAME="admin"
DB_PASSWORD="your-password"

# 기존 컨테이너 정리
docker stop erp-app || true
docker rm erp-app || true

# 새 컨테이너 실행
docker run -d \
  --name erp-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://${RDS_ENDPOINT}:3306/${DB_NAME} \
  -e SPRING_DATASOURCE_USERNAME=${DB_USERNAME} \
  -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
  ${DOCKER_IMAGE}
```

## 4. 데이터베이스 마이그레이션

### 4.1 Flyway 설정
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

### 4.2 마이그레이션 실행
```bash
# 애플리케이션 첫 실행 시 자동 마이그레이션
java -jar app.jar --spring.profiles.active=prod
```

## 5. 모니터링 및 로깅

### 5.1 애플리케이션 로그
- 로그 위치: /var/log/erp-app/
- Docker 로그: docker logs erp-app

### 5.2 데이터베이스 모니터링
- RDS 모니터링 대시보드
- CloudWatch 메트릭스

## 6. 백업 및 복구

### 6.1 데이터베이스 백업
- RDS 자동 백업 설정
  - 백업 보관 기간: 7일
  - 백업 시간: 새벽 시간대

### 6.2 애플리케이션 백업
- Docker 이미지 태깅
- 이전 버전 보관

## 7. 보안 설정

### 7.1 네트워크 보안
```
EC2 Security Group:
- Inbound:
  - 22/TCP (SSH): 관리자 IP
  - 80/TCP (HTTP): 0.0.0.0/0
  - 443/TCP (HTTPS): 0.0.0.0/0
  - 8080/TCP (Application): 0.0.0.0/0

RDS Security Group:
- Inbound:
  - 3306/TCP (MySQL): EC2 Security Group
```

### 7.2 애플리케이션 보안
- JWT 토큰 설정
- HTTPS 적용 (선택사항)
- 환경 변수 관리

## 8. 스케일링 고려사항

### 8.1 수직적 스케일링
- EC2 인스턴스 타입 변경
- RDS 인스턴스 클래스 변경

### 8.2 수평적 스케일링 (향후)
- EC2 Auto Scaling
- RDS Read Replica

## 9. 비용 최적화

### 9.1 예상 비용 (월간)
- EC2 (t3.small): $16.50
- RDS (db.t3.micro): $12.50
- EBS: $2-3
- 데이터 전송: $1-2
총 예상 비용: $30-35/월

### 9.2 비용 절감 방안
- 예약 인스턴스 사용
- 자동 중지/시작 스케줄링
- 리소스 모니터링