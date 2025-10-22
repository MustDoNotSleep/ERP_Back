// config/dbConfig.js

// ✅ 1. mysql2 패키지 로드 (Promise 기반 MySQL 사용을 권장)
import mysql from 'mysql2/promise';

// ✅ 2. Lambda 환경변수 불러오기
const {
  DB_HOST,
  DB_USER,
  DB_PASSWORD,
  DB_NAME,
  DB_PORT = 3306, // 기본값 지정
} = process.env;

// ✅ 3. 연결 옵션 정의
// Lambda는 매번 새로 함수가 실행되므로 연결을 효율적으로 재사용해야 함
const dbConfig = {
  host: DB_HOST,
  user: DB_USER,
  password: DB_PASSWORD,
  database: DB_NAME,
  port: DB_PORT,
  waitForConnections: true,
  connectionLimit: 10,   // 동시 연결 허용 개수
  queueLimit: 0,         // 대기열 무제한
  timezone: '+09:00',    // 한국 시간 기준 설정
};

// ✅ 4. 커넥션 풀 생성 (Lambda의 가장 상단에서 한 번만 생성 → 재사용)
let pool;

export const getPool = () => {
  if (!pool) {
    pool = mysql.createPool(dbConfig);
    console.log('✅ MySQL Pool created');
  }
  return pool;
};

// ✅ 5. 유틸성 검증 함수 (선택)
// 연결이 정상인지 테스트할 때 사용 가능
export const testDBConnection = async () => {
  try {
    const connection = await getPool().getConnection();
    await connection.ping(); // 연결 확인
    connection.release();
    console.log('✅ DB connection successful');
  } catch (error) {
    console.error('❌ DB connection failed:', error.message);
    throw error;
  }
};
