// const mysql = require('mysql2/promise');

// // 💡 중요: RDS 접속 정보는 코드에 직접 노출하지 않고 환경 변수로 관리합니다.
// const dbConfig = {
//     host: process.env.RDS_HOST,          // RDS 엔드포인트
//     user: process.env.RDS_USER,          // RDS 마스터 사용자 이름
//     password: process.env.RDS_PASSWORD,  // RDS 마스터 사용자 비밀번호
//     database: process.env.RDS_DATABASE,  // 우리가 사용하는 데이터베이스 이름 (예: erp_database)
//     waitForConnections: true,
//     connectionLimit: 10,
//     queueLimit: 0
// };

// // 💡 연결 풀 생성: Lambda 콜드 스타트 시 연결 시간을 줄여줍니다.
// const pool = mysql.createPool(dbConfig);

// /**
//  * 연결 풀에서 연결을 얻어와 쿼리를 실행하는 헬퍼 함수
//  */
// const executeQuery = async (query, params = []) => {
//     let connection;
//     try {
//         // 풀에서 연결 하나 가져오기
//         connection = await pool.getConnection();
//         const [rows] = await connection.execute(query, params);
//         return rows;
//     } catch (error) {
//         console.error('MySQL Query Error:', error);
//         throw error;
//     } finally {
//         // 연결 반납
//         if (connection) connection.release();
//     }
// };

// module.exports = {
//     executeQuery,
// };