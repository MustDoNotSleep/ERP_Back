const { PutCommand, QueryCommand, UpdateCommand, DeleteCommand } = require("@aws-sdk/lib-dynamodb");
const { docClient, TABLE_NAME } = require('../utils/db/dynamoDbClient'); 
const { executeQuery } = require('../utils/db/mysqlClient'); 

/**
 * 공통 유효성 검증 및 사원 정보 조회 함수
 * @returns {Promise<object>} 사원 정보 객체 (존재하지 않으면 에러 발생)
 */
const getEmployeeInfo = async (employeeId) => {
    // 💡 모든 필드를 조회하도록 SELECT * 사용
    const sql = `
        SELECT employeeName, departmentId, positionId, email, hireDate 
        FROM Employees 
        WHERE employeeId = ?
    `;
    const params = [employeeId];
    
    // executeQuery는 row 배열을 반환합니다.
    const result = await executeQuery(sql, params); 
    
    // 조회 결과가 없으면 에러를 발생시킵니다.
    if (!result || result.length === 0) {
        throw new Error(`사원 ID ${employeeId}는 존재하지 않습니다.`);
    }
    
    return result[0]; 
}

/**
 * DynamoDB에 출근 기록을 저장하는 핵심 로직
 */
const clockIn = async (employeeId, attendanceType) => { 
    // 1. 시간 및 키 생성
    await validateEmployeeExists(employeeId); 

    const now = new Date();
    const todayDate = now.toISOString().split('T')[0];
    const clockInTime = now.toLocaleTimeString('en-GB', { hour12: false });

    const PK = `EMP#${employeeId}`;
    const SK = `ATTENDANCE#${todayDate}#${clockInTime}`;

    // 2. DynamoDB 저장 명령어 생성
    const command = new PutCommand({
        TableName: TABLE_NAME,
        Item: {
            PK: PK,
            SK: SK,
            Type: 'Attendance', 
            
            employeeId: employeeId, 
            clockIn: clockInTime,     
            date: todayDate,           
            attendanceType: attendanceType, 
        },
    });

    // 3. DynamoDB 요청 전송
    await docClient.send(command);

    return {
        message: `출근 기록 완료: ${todayDate} ${clockInTime}`,
    };
};

/**
 * DynamoDB에 퇴근 기록을 저장하는 핵심 로직
 */
const clockOut = async (employeeId, date, clockOutTime) => {
    // 1. 당일 출근 기록 조회 (가장 최근 출근 기록을 찾기 위해 Query 사용)
    await validateEmployeeExists(employeeId); 

    const PK = `EMP#${employeeId}`;
    const SK_PREFIX = `ATTENDANCE#${date}`; // 예: ATTENDANCE#2025-10-15
    
    const queryCommand = new QueryCommand({
        TableName: TABLE_NAME,
        KeyConditionExpression: "PK = :pk AND begins_with(SK, :skPrefix)",
        FilterExpression: "attribute_not_exists(clockOut)", // 퇴근 시간이 없는 기록만 찾기
        ExpressionAttributeValues: {
            ":pk": PK,
            ":skPrefix": SK_PREFIX,
        },
        // 가장 최근 출근 기록 (PK 내에서 SK는 시간순) 1개만 가져옴
        Limit: 1, 
    });

    const queryResult = await docClient.send(queryCommand);
    const latestRecord = queryResult.Items ? queryResult.Items[0] : null;

    if (!latestRecord) {
        throw new Error("오늘 출근 기록을 찾을 수 없거나 이미 퇴근 처리되었습니다.");
    }

    // 2. 퇴근 시간 업데이트 (UpdateCommand 사용)
    const updateCommand = new UpdateCommand({
        TableName: TABLE_NAME,
        Key: { // 업데이트할 항목의 PK와 SK를 지정
            PK: latestRecord.PK,
            SK: latestRecord.SK,
        },
        UpdateExpression: "SET clockOut = :co, updatedAt = :updatedAt",
        ExpressionAttributeValues: {
            ":co": clockOutTime,
            ":updatedAt": new Date().toISOString(),
        },
        ConditionExpression: "attribute_not_exists(clockOut)", // 퇴근 시간이 없어야만 업데이트
    });

    await docClient.send(updateCommand);

    return {
        message: `퇴근 기록 완료: ${clockOutTime}`,
    };
};

/**
 * DynamoDB에서 특정 사원의 월별 근태 기록을 조회합니다.
 */
const getAttendanceRecords = async (employeeId, yearMonth) => {
    await validateEmployeeExists(employeeId); 

    const PK = `EMP#${employeeId}`;
    const SK_PREFIX = `ATTENDANCE#${yearMonth}`; // YYYY-MM 형식 (예: ATTENDANCE#2025-10)
    
    const command = new QueryCommand({
        TableName: TABLE_NAME,
        KeyConditionExpression: "PK = :pk AND begins_with(SK, :skPrefix)", // PK와 SK 접두사로 조회
        ExpressionAttributeValues: {
            ":pk": PK,
            ":skPrefix": SK_PREFIX,
        },
        // SK가 시간순으로 정렬되어 있으므로, 최신순으로 보려면 Descending=true
        ScanIndexForward: false, // 최신 기록부터 보여줌 (내림차순)
    });

    const result = await docClient.send(command);

    // DynamoDB Item의 배열을 반환합니다.
    return result.Items; 
};

/**
 * DynamoDB에 존재하는 근태 기록을 전체 필드로 덮어씁니다. (Update 기능)
 */
const updateAttendanceRecord = async (data) => {
    // 1. PK와 SK를 기존 데이터에서 가져옵니다. (수정할 항목 식별)
    await validateEmployeeExists(employeeId); 

    const { employeeId, date, clockInTime } = data; // 이 3가지 키로 항목을 찾습니다.
    
    // DB의 PK/SK 형식으로 재구성
    const PK = `EMP#${employeeId}`;
    const SK = `ATTENDANCE#${date}#${clockInTime}`;  // 기존 기록과 일치해야 함!

    // 2. DynamoDB PutCommand를 사용해 기존 항목을 덮어씁니다.
    const command = new PutCommand({
        TableName: TABLE_NAME,
        Item: {
            // PK와 SK는 그대로 유지
            PK: PK,
            SK: SK,
            Type: 'Attendance', 
            
            // 🚨 업데이트될 모든 필드를 Item에 다시 넣어줍니다.
            employeeId: employeeId,
            clockIn: data.clockIn,
            clockOut: data.clockOut || null, // 퇴근 시간이 없을 수도 있음
            date: date,
            attendanceType: data.attendanceType,
            
            // 추가 필드 (필요하다면)
            updatedAt: new Date().toISOString(),
        },
    });

    // 3. DynamoDB 요청 전송
    await docClient.send(command);

    return {
        message: `근태 기록 (${date})이 성공적으로 수정되었습니다.`,
    };
};

/**
 * DynamoDB에서 특정 사원의 출근 기록 하나를 삭제합니다. (Delete 기능)
 */
const deleteAttendanceRecord = async (employeeId, date, clockInTime) => {
    // 1. PK와 SK 생성 (삭제할 항목을 정확히 식별)
    await validateEmployeeExists(employeeId); 

    const PK = `EMP#${employeeId}`;
    const SK = `ATTENDANCE#${date}#${clockInTime}`;

    // 2. DynamoDB 삭제 명령어 생성
    const command = new DeleteCommand({
        TableName: TABLE_NAME,
        Key: {
            PK: PK,
            SK: SK,
        },
    });

    // 3. DynamoDB 요청 전송
    await docClient.send(command);

    return {
        message: `근태 기록이 성공적으로 삭제되었습니다: ${date} ${clockInTime}`,
    };
};

module.exports = {
    clockIn,
    clockOut,
    getAttendanceRecords,
    updateAttendanceRecord, 
    deleteAttendanceRecord,
};