// const AttendanceService = require('../services/AttendanceService'); 
// // 💡 기존 handler는 clockIn 요청을 처리합니다.
// exports.clockInHandler = async (event) => {
//     try {
//         const body = JSON.parse(event.body);
//         const employeeId = body.employeeId; 
//         const attendanceType = body.attendanceType || '정상근무'; 
        
//         if (!employeeId) {
//             return { statusCode: 400, body: JSON.stringify({ message: '사원 ID (employeeId)가 누락되었어요.' }) };
//         }

//         const result = await AttendanceService.clockIn(employeeId, attendanceType);

//         return {
//             statusCode: 201,
//             body: JSON.stringify(result),
//         };

//     } catch (error) {
//         console.error('Lambda 처리 에러:', error);
//         return {
//             statusCode: 500,
//             body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
//         };
//     }
// };

// /**
//  * 근태 퇴근 기록 (Clock Out) 요청을 처리하는 핸들러입니다.
//  */
// exports.clockOutHandler = async (event) => {
//     try {
//         const body = JSON.parse(event.body);
//         const employeeId = body.employeeId;
        
//         if (!employeeId) {
//             return { statusCode: 400, body: JSON.stringify({ message: '사원 ID (employeeId)가 누락되었어요.' }) };
//         }

//         // 현재 시간 생성
//         const now = new Date();
//         const todayDate = now.toISOString().split('T')[0];
//         const clockOutTime = now.toLocaleTimeString('en-GB', { hour12: false });

//         // Service Layer의 clockOut 함수 호출
//         const result = await AttendanceService.clockOut(employeeId, todayDate, clockOutTime);

//         return {
//             statusCode: 200, // 업데이트는 200 또는 204
//             body: JSON.stringify(result),
//         };

//     } catch (error) {
//         console.error('Lambda 처리 에러:', error);
//         return {
//             statusCode: 500,
//             body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
//         };
//     }
// };

// /**
//  * 근태 기록 조회 (Read) 요청을 처리하는 새로운 핸들러입니다.
//  * @param {object} event - API Gateway 이벤트 (쿼리스트링에서 파라미터를 읽습니다)
//  */
// exports.getRecordsHandler = async (event) => {
//     try {
//         // 💡 쿼리스트링 파라미터 읽기 (API Gateway GET 요청 가정)
//         const queryStringParameters = event.queryStringParameters || {};
//         const employeeId = queryStringParameters.employeeId;
//         const yearMonth = queryStringParameters.yearMonth; // YYYY-MM 형식 (예: 2025-10)

//         if (!employeeId || !yearMonth) {
//             return { statusCode: 400, body: JSON.stringify({ message: '사원 ID와 조회 연월이 누락되었어요.' }) };
//         }

//         // 💡 Service Layer의 조회 함수 호출
//         const records = await AttendanceService.getAttendanceRecords(employeeId, yearMonth);

//         // 💡 응답 반환
//         return {
//             statusCode: 200,
//             body: JSON.stringify(records),
//         };

//     } catch (error) {
//         console.error('Lambda 조회 에러:', error);
//         return {
//             statusCode: 500,
//             body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
//         };
//     }
// };

// /**
//  * 근태 기록 전체 수정 (Update) 요청을 처리하는 핸들러입니다.
//  */
// exports.updateRecordHandler = async (event) => {
//     try {
//         const body = JSON.parse(event.body);
//         const { employeeId, date, clockInTime, clockOut, attendanceType } = body;

//         // 필수 키와 수정 데이터 검증 (기존 PK/SK와 수정될 내용)
//         if (!employeeId || !date || !clockInTime || !attendanceType) {
//             return { 
//                 statusCode: 400, 
//                 body: JSON.stringify({ message: '필수 수정 정보(사원ID, 날짜, 출근시각, 유형)가 누락되었어요.' }) 
//             };
//         }
        
//         // Service Layer의 수정 함수 호출
//         const result = await AttendanceService.updateAttendanceRecord(body); // body 전체를 Service로 전달

//         return {
//             statusCode: 200, // 수정 성공
//             body: JSON.stringify(result),
//         };

//     } catch (error) {
//         console.error('Lambda 수정 에러:', error);
//         return {
//             statusCode: 500,
//             body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
//         };
//     }
// };


// /**
//  * 근태 기록 삭제 (Delete) 요청을 처리하는 핸들러입니다.
//  */
// exports.deleteRecordHandler = async (event) => {
//     try {
//         // DELETE 요청의 경우 body에서 PK/SK를 구성할 정보를 가져옵니다.
//         const body = JSON.parse(event.body);
//         const { employeeId, date, clockInTime } = body;

//         // 필수 값 검증
//         if (!employeeId || !date || !clockInTime) {
//             return { 
//                 statusCode: 400, 
//                 body: JSON.stringify({ message: '사원 ID, 날짜, 출근 시간이 모두 필요해요.' }) 
//             };
//         }

//         // Service Layer의 삭제 함수 호출
//         const result = await AttendanceService.deleteAttendanceRecord(
//             employeeId,
//             date,
//             clockInTime
//         );

//         return {
//             statusCode: 200, // 삭제 성공
//             body: JSON.stringify(result),
//         };

//     } catch (error) {
//         console.error('Lambda 삭제 에러:', error);
//         return {
//             statusCode: 500,
//             body: JSON.stringify({ error: '서버 에러 발생 삐약!', detail: error.message }),
//         };
//     }
// };
