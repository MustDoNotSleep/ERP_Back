// handlers/changePasswordHandler.js
import { getPool } from '../config/dbConfig.js';
import { buildResponse } from '../utils/loginresponse.js';
import jwt from 'jsonwebtoken'

export const changePasswordHandler = async (event) => {
  console.log("🔒 Change Password:", JSON.stringify(event, null, 2));
  
  const pool = getPool();
  let requesterId;
  try {
    // 1. JWT 토큰 검증 및 employeeId 추출 (가장 중요한 보안 조치)
    const token = event.headers.Authorization?.split(' ')[1];
    if (!token) {
        return buildResponse(401, { error: 'Unauthorized: No token provided.' });
    }

    let decoded;
    try {
        // 🚨 환경 변수 JWT_SECRET을 사용하여 토큰 검증
        decoded = jwt.verify(token, process.env.JWT_SECRET);
    } catch (err) {
        return buildResponse(401, { error: 'Unauthorized: Invalid token.' });
    }
    
    // 🔑 FIX 1: 토큰에서 요청자의 ID를 추출 (body 대신 토큰 사용)
    requesterId = decoded.employeeId;

    const body = JSON.parse(event.body || '{}');
    const { currentPassword, newPassword } = body;

    // 입력값 검증
    if (!currentPassword || !newPassword) {
      return buildResponse(400, { 
        error: 'Employee ID, current password, and new password are required.' 
      });
    }

    // 비밀번호 길이 검증
    if (newPassword.length < 8) {
      return buildResponse(400, { 
        error: 'New password must be at least 20 characters long.' 
      });
    }
    // 현재 비밀번호 확인
    const [rows] = await pool.query(
      'SELECT password FROM Employees WHERE employeeId = ?',
      [requesterId]
    );

    if (rows.length === 0) {
      return buildResponse(404, { error: 'Employee not found.' });
    }

    const employee = rows[0];

    // 현재 비밀번호 검증 (평문 비교)
    if (employee.password !== currentPassword) {
      return buildResponse(401, { error: 'Current password is incorrect.' });
    }

    // 새 비밀번호로 업데이트 (평문 저장)
    await pool.query(
      'UPDATE Employees SET password = ? WHERE employeeId = ?',
      [newPassword, requesterId]
    );

    return buildResponse(200, {
      message: 'Password changed successfully'
    });

  } catch (err) {
    console.error('ChangePasswordHandler Error:', err.message);
    console.error('Stack:', err.stack);
    return buildResponse(500, { error: `Internal error: ${err.message}` });
  }
};