// config/awsConfig.js

// ✅ 1. AWS SDK v3에서 사용할 서비스 클라이언트 로드
import { S3Client } from "@aws-sdk/client-s3";
import { SNSClient } from "@aws-sdk/client-sns";
import { SQSClient } from "@aws-sdk/client-sqs";
import { EventBridgeClient } from "@aws-sdk/client-eventbridge";

// ✅ 2. 환경변수에서 AWS 기본 정보 불러오기
import { ENV } from "./env.js";

const AWS_REGION = ENV.AWS_REGION || "ap-northeast-2"; // 기본 리전: 서울

// ✅ 3. AWS SDK 공통 설정 정의
// Lambda에서는 IAM Role을 사용하므로 credentials 불필요
export const AWS_CONFIG = {
  region: AWS_REGION,
  maxAttempts: 3, // 재시도 횟수
  retryMode: "standard", // 재시도 전략
};

// ✅ 4. 주요 AWS 서비스 클라이언트 객체 생성
export const s3Client = new S3Client(AWS_CONFIG);
export const snsClient = new SNSClient(AWS_CONFIG);
export const sqsClient = new SQSClient(AWS_CONFIG);
export const eventBridgeClient = new EventBridgeClient(AWS_CONFIG);

// ✅ 5. 필요시 다른 서비스도 추가 가능 (예: SES, Lambda, CloudWatch 등)
// import { SESClient } from "@aws-sdk/client-ses";
// export const sesClient = new SESClient(AWS_CONFIG);

/**
 * 💡 이 awsConfig.js의 목적
 * 1. 모든 AWS SDK 클라이언트가 동일한 region, retry 정책을 공유하도록 함.
 * 2. Lambda 코드 어디서든 바로 import 가능 (예: s3Client.send(command)).
 * 3. 인증은 Lambda의 IAM Role이 자동 해결 → 보안상 안전.
 */
