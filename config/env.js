// config/env.js

// ✅ 1. 환경변수 로드
// Lambda에서는 process.env에 바로 포함되어 있음
// 로컬 개발 시 dotenv를 사용하도록 조건부로 로드
//if (process.env.NODE_ENV !== 'production') {
 //   import('dotenv').then(dotenv => dotenv.config());
 // }
  
  // ✅ 2. 공통 환경 변수 구조화
  export const ENV = {
    // Lambda 실행 환경
    NODE_ENV: process.env.NODE_ENV || 'development',
  
    // 📦 AWS 기본 설정
    AWS_REGION: process.env.AWS_REGION || 'ap-northeast-2',
  
    // 🧩 MySQL (RDS)
    DB: {
      HOST: process.env.DB_HOST,
      USER: process.env.DB_USER,
      PASSWORD: process.env.DB_PASSWORD,
      NAME: process.env.DB_NAME,
      PORT: process.env.DB_PORT || 3306,
    },
  
    // ⚡️ DynamoDB
    DYNAMO: {
      TABLE_NAME: process.env.DYNAMO_TABLE_NAME || 'Dynamo_ERP_DATA',
    },
  
    // 기타 (필요 시 확장 예: S3, API_KEY 등)
    LOG_LEVEL: process.env.LOG_LEVEL || 'info',
  };
  