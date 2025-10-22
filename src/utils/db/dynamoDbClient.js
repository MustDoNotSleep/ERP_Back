const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const { DynamoDBDocumentClient } = require("@aws-sdk/lib-dynamodb");

// 💡 DynamoDB 클라이언트 인스턴스를 한 번만 생성하여 재사용 (콜드 스타트 최소화)
const client = new DynamoDBClient({});

// DocClient는 데이터베이스 작업을 쉽게 해주는 래퍼 클라이언트입니다.
const docClient = DynamoDBDocumentClient.from(client);

module.exports = {
    docClient,
    TABLE_NAME: process.env.DYNAMO_TABLE || 'Dynamo_ERP_DATA', // 환경 변수 또는 기본값 사용
};