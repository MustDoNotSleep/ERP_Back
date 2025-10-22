// config/index.js

// ✅ 1. 환경 변수 관련 설정 불러오기
import { ENV } from './env.js';

// ✅ 2. 데이터베이스 (RDS MySQL)
import { getPool, testDBConnection } from './dbConfig.js';

// ✅ 3. DynamoDB 설정
import { dynamoDocClient, DYNAMO_CONFIG } from './dynamodbConfig.js';

// ✅ 4. AWS 공통 설정
import {
  AWS_CONFIG,
  s3Client,
  snsClient,
  sqsClient,
  eventBridgeClient,
} from './awsConfig.js';

// ✅ 5. 설정 및 클라이언트 통합 export
export {
  // 환경 변수
  ENV,

  // MySQL (RDS)
  getPool,
  testDBConnection,

  // DynamoDB
  dynamoDocClient,
  DYNAMO_CONFIG,

  // AWS 서비스 설정
  AWS_CONFIG,
  s3Client,
  snsClient,
  sqsClient,
  eventBridgeClient,
};
