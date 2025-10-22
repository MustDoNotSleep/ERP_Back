// const { PutCommand } = require("@aws-sdk/lib-dynamodb");
// const { docClient, TABLE_NAME } = require('../utils/db/dynamoDbClient'); 

// const requestLeave = async (data) => {
//     // 💡 data에서 CamelCase로 속성을 분해합니다.
//     const { employeeId, leaveType, startDate, endDate, reason, requestDays } = data; 

//     const request_id = `${Date.now().toString().slice(-6)}-${employeeId}`; // PK/SK를 위한 보조 변수
    
//     const PK = `EMP#${employeeId}`; 
//     const SK = `LEAVE#${request_id}`; 

//     const command = new PutCommand({
//         TableName: TABLE_NAME,
//         Item: {
//             PK: PK,
//             SK: SK,
//             Type: 'LeaveRequest',
            
//             // 🚨 Item 속성 전체를 CamelCase로 통일
//             requestId: request_id, 
//             employeeId: employeeId, 
//             leaveType: leaveType,
//             startDate: startDate,
//             endDate: endDate,
//             reason: reason || '사유 없음',
//             status: 'Pending', 
//             requestDate: new Date().toISOString().split('T')[0], 
//             requestDays: requestDays 
//         },
//     });

//     await docClient.send(command);

//     return {
//         message: `휴가 신청이 접수되었습니다. (ID: ${request_id})`,
//     };
// };

// module.exports = {
//     requestLeave,
// };