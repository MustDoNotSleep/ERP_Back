const PostService = require('../src/services/PostService'); 

exports.handler = async (event) => {
    try {
        const data = JSON.parse(event.body);
        
        // 필수 유효성 검사
        if (!data.employeeId || !data.title || !data.content) {
            return { statusCode: 400, body: JSON.stringify({ message: '필수 공지 정보(작성자, 제목, 내용)가 누락되었어요.' }) };
        }

        const result = await PostService.createPost(data);

        return {
            statusCode: 201,
            body: JSON.stringify(result),
        };

    } catch (error) {
        console.error('Lambda 처리 에러:', error);
        return {
            statusCode: 500,
            body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
        };
    }
};