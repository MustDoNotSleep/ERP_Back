const LeaveService = require('../src/services/LeaveService'); 

exports.handler = async (event) => {
    try {
        const data = JSON.parse(event.body);
        
        // 필수 유효성 검사
        if (!data.employeeId || !data.leaveType || !data.startDate || !data.endDate) {
            return { statusCode: 400, body: JSON.stringify({ message: '필수 신청 정보(사원ID, 유형, 기간)가 누락되었어요.' }) };
        }

        // Service Layer 호출
        const result = await LeaveService.requestLeave(data);

        // 응답 반환
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