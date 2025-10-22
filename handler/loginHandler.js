// handlers/loginHandler.js
import mysql from 'mysql2/promise';
import { getPool } from '../config/dbConfig.js';
import bcrypt from 'bcryptjs'; // <-- bcrypt 임포트 제거
import jwt from 'jsonwebtoken';
import { buildResponse } from '../utils/loginresponse.js';

export const loginHandler = async (event) => {
  console.log("Received event:", JSON.stringify(event, null, 2));
  try {
    // 1. 프론트에서 전달된 body(JSON)를 파싱
    const body = JSON.parse(event.body || '{}');
    const { email, password } = body;

    // 2. 입력값 검증
    if (!email || !password) {
      return buildResponse(400, { error: 'Email and password are required.' });
    }

    const pool = getPool();

    // 3. 🚀 [수정] teamName을 포함하도록 SQL 쿼리 변경
    const [rows] = await pool.query(
      `SELECT 
        e.employeeId, e.name, e.email, e.password, 
        p.positionLevel, d.teamName 
       FROM Employees e 
       LEFT JOIN Positions p ON e.positionId = p.positionId
       LEFT JOIN Department d ON e.departmentId = d.departmentId
       WHERE e.email = ?`,
      [email]
    );

    // 4. 사용자가 없을 때
    if (rows.length === 0) {
      return buildResponse(401, { error: 'Invalid email or password.' });
    }

    const user = rows[0];

    // 5. 🚨 [수정] 평문 비밀번호 비교 (요청사항 반영)
  if (user.password !== password) {
    return buildResponse(401, { error: 'Invalid email or password.' });
}
    // 6. 🚀 [수정] JWT Payload에 teamName 추가
    const payload = {
      employeeId: user.employeeId,
      email: user.email,
      name: user.name,
      positionLevel: user.positionLevel,
      teamName: user.teamName // 권한 검증을 위한 teamName 추가
    };

    // 7. 토큰 서명 (유효기간: 3시간)
    const token = jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: '3h' });

    // 8. 성공 응답
    return buildResponse(200, {
      message: 'login successful',
      user: {
        employeeId: user.employeeId,
        email: user.email,
        name: user.name,
        teamName : user.teamName,
        employmentType:user.employmentType,
      },
      token: token
    });
  } catch (err) {
    console.error('LoginHandler Error:', err.message);
    console.error('Stack:', err.stack);
    return buildResponse(500, { error: `Internal error: ${err.message}` });
  }
};