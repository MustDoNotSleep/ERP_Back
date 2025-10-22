// config/dynamoConfig.js

// ✅ 1. AWS SDK v3 로드
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

// ✅ 2. 환경변수에서 region, tableName 등을 가져오기
// (region은 Lambda 실행 리전 기반으로 자동 설정 가능)
const {
  AWS_REGION = "ap-northeast-2",  // 서울 리전 기본값
  DYNAMO_TABLE_NAME = "Dynamo_ERP_DATA", // 사용 중인 테이블 이름
} = process.env;

// ✅ 3. 기본 DynamoDB 클라이언트 생성
const baseClient = new DynamoDBClient({
  region: AWS_REGION,
  // Lambda에서 IAM Role을 통해 인증하므로 credentials 명시 불필요
});

// ✅ 4. DocumentClient 래퍼 생성 (JSON 변환 자동 처리)
export const dynamoDocClient = DynamoDBDocumentClient.from(baseClient, {
  marshallOptions: {
    removeUndefinedValues: true, // undefined 값 제외
  },
  unmarshallOptions: {
    wrapNumbers: false, // 숫자 값 자동 변환
  },
});

// ✅ 5. 헬퍼 상수 및 유틸 export
export const DYNAMO_CONFIG = {
  region: AWS_REGION,
  tableName: DYNAMO_TABLE_NAME,
};
