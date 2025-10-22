const { PutCommand } = require("@aws-sdk/lib-dynamodb");
const { docClient, TABLE_NAME } = require('../utils/db/dynamoDbClient');

/**
 * DynamoDB에 새로운 공지사항을 저장합니다.
 */
const createPost = async (data) => {
    // 고유 ID 생성 및 시간 기록
    const postId = `POST#${Date.now()}`; 
    const createdAt = new Date().toISOString(); 

    const PK = postId;
    const SK = 'METADATA'; // 공지사항 기본 정보는 METADATA로 통일

    const command = new PutCommand({
        TableName: TABLE_NAME,
        Item: {
            PK: PK,
            SK: SK,
            Type: 'Post', 
            
            // 공지사항 속성 (CamelCase 표준)
            postId: postId,
            employeeId: data.employeeId, // 작성자 ID
            title: data.title,
            content: data.content,
            attachedFile: data.attachedFile || null,
            
            createdAt: createdAt, // ERD의 created_date
            updatedAt: createdAt, // ERD의 updated_date
            
            // GSI를 위한 속성 (전체 공지 목록 조회용)
            GSI1PK: 'TYPE#POST', // 모든 공지를 이 키로 묶습니다.
            GSI1SK: createdAt,   // 생성일자 기준으로 정렬하여 최신순 조회가 가능하게 합니다.
        },
    });

    await docClient.send(command);

    return {
        message: `공지사항이 성공적으로 등록되었습니다. (ID: ${postId})`,
    };
};

module.exports = {
    createPost,
};