# ERP_Back

/ERP_BACK
├── node_modules/         <-- 💡 설치된 라이브러리 
├── package.json          <-- 💡 프로젝트 설정 파일
├── serverless.yml        <-- 💡 (선택사항: 나중에 AWS 배포 자동화에 사용)
|
├── handlers/             <-- 💡 [CONTROLLER LAYER] - Lambda 함수 진입점 (Handler)
│   ├── attendanceHandler.js  <-- 근태: 출/퇴근 요청 처리 (exports.handler)
│   ├── leaveHandler.js       <-- 휴가: 신청/조회 요청 처리
│   ├── postHandler.js        <-- 공지: 생성/조회 요청 처리
│   └── salaryHandler.js      <-- 급여: 급여 정보 생성/조회 요청 처리
|
└── src/
    ├── services/             <-- 💡 [SERVICE LAYER] - 핵심 비즈니스 로직 및 트랜잭션
    │   ├── AttendanceService.js  <-- DynamoDB 근태 CRUD 로직
    │   ├── LeaveService.js       <-- DynamoDB 휴가 CRUD 로직
    │   ├── PostService.js        <-- DynamoDB 공지 CRUD 로직
    │   └── SalaryService.js      <-- MySQL 급여 CRUD 로직
    |
    ├── models/               <-- 💡 [MODEL / DTO LAYER] - 데이터 유효성 검사 및 구조 정의
    │   ├── AttendanceDTO.js  <-- 근태 입력/출력 데이터 구조
    │   ├── LeaveDTO.js       <-- 휴가 입력 데이터 구조
    │   ├── PostDTO.js        <-- 공지 입력 데이터 구조
    │   └── SalaryDTO.js      <-- 급여 입력 데이터 구조
    |
    └── utils/                <-- 💡 [UTILITY LAYER] - 공통 기능 (DB 연결, 에러 처리 등)
        ├── db/
        │   ├── dynamoDbClient.js <-- DynamoDB 클라이언트 생성 및 재사용
        │   └── mysqlClient.js    <-- MySQL 연결 풀 생성 및 재사용
        └── error/
            └── CustomError.js    <-- 커스텀 에러 클래스 정의 (400, 500 등)