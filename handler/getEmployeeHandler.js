// handlers/getEmployeeHandler.js
import { getPool } from '../config/dbConfig.js';
import { buildResponse } from '../utils/loginresponse.js';
import jwt from 'jsonwebtoken';

// 권한 상수 정의
const MANAGER_LEVEL = 6;
const HR_TEAM_NAME = '인사팀';

const fetchFullEmployeeDetails = async (pool, employeeId) => {
  // ... (기존 코드 동일)
  const [empRows] = await pool.query(
    `SELECT 
      e.*, 
      d.departmentName, d.teamName, 
      p.positionName 
     FROM Employees e
     LEFT JOIN Department d ON e.departmentId = d.departmentId
     LEFT JOIN Positions p ON e.positionId = p.positionId
     WHERE e.employeeId = ?`,
    [employeeId]
  );

  if (empRows.length === 0) return null;

  const employee = empRows[0];

  const [
    [militaryRows], 
    [salaryRows]
  ] = await Promise.all([
    pool.query('SELECT * FROM MilitaryServiceInfo WHERE employeeId = ?', [employeeId]),
    pool.query('SELECT * FROM SalaryInfo WHERE employeeId = ?', [employeeId])
  ]);

  const [
    [educationRows], 
    [workExpRows], 
    [certRows]
  ] = await Promise.all([
    pool.query('SELECT * FROM Education WHERE employeeId = ?', [employeeId]),
    pool.query('SELECT * FROM WorkExperience WHERE employeeId = ?', [employeeId]),
    pool.query('SELECT * FROM Certificates WHERE employeeId = ?', [employeeId])
  ]);

  delete employee.password;
  delete employee.rrn;

  return {
    ...employee,
    militaryInfo: militaryRows[0] || null,
    salaryInfo: salaryRows[0] || null,
    educations: educationRows,
    workExperiences: workExpRows,
    certificates: certRows
  };
};

// --- 메인 핸들러 ---
export const getEmployeeHandler = async (event) => {
  console.log("📋 Get Employee Info:", event.httpMethod, event.path);
  const pool = getPool();

  try {
    // 1. JWT 토큰 검증
    const token = event.headers.Authorization?.split(' ')[1];
    if (!token) {
      return buildResponse(401, { error: 'Unauthorized: No token provided.' });
    }
    
    let decoded;
    try {
      decoded = jwt.verify(token, process.env.JWT_SECRET);
    } catch (err) {
      return buildResponse(401, { error: 'Unauthorized: Invalid token.' });
    }
    
    const requesterId = decoded.employeeId;
    const requesterLevel = decoded.positionLevel;
    const requesterTeam = decoded.teamName;

    // 2. 요청자 부서 조회
    const [requesterRows] = await pool.query(
      'SELECT departmentId FROM Employees WHERE employeeId = ?',
      [requesterId]
    );
    if (requesterRows.length === 0) {
      return buildResponse(403, { error: 'Forbidden: Requester not found.' });
    }
    const requesterDeptId = requesterRows[0].departmentId;

    // 3. 권한 체크
    const isHRManager = (requesterTeam === HR_TEAM_NAME) && (requesterLevel >= MANAGER_LEVEL);
    const isTeamManager = (requesterTeam !== HR_TEAM_NAME) && (requesterLevel >= MANAGER_LEVEL);

    // 4. 경로 분석
    const targetId = event.pathParameters?.employeeId;

    // ===== CASE A: 전체 직원 목록 조회 =====
    if (!targetId) {
      const filters = event.queryStringParameters || {};
      let whereClauses = ['e.quitDate IS NULL'];
      let queryParams = [];

      // 권한 검사
      if (isHRManager) {
        // 인사팀장: 전체 접근
      } else if (isTeamManager) {
        // 팀 관리자: 자기 부서만
        whereClauses.push('e.departmentId = ?');
        queryParams.push(requesterDeptId);
      } else {
        // 일반 직원: 목록 조회 불가
        return buildResponse(403, { 
          error: 'Forbidden: You do not have permission to view all employees.' 
        });
      }

      // 필터링
      if (filters.name) {
        whereClauses.push('e.name LIKE ?');
        queryParams.push(`%${filters.name}%`);
      }
      if (filters.employeeId) {
        whereClauses.push('e.employeeId = ?');
        queryParams.push(filters.employeeId);
      }
      if (filters.positionName) {
        whereClauses.push('p.positionName = ?');
        queryParams.push(filters.positionName);
      }
      if (filters.teamName) {
        whereClauses.push('d.teamName = ?');
        queryParams.push(filters.teamName);
      }

      const whereSql = `WHERE ${whereClauses.join(' AND ')}`;
      
      const query = `
        SELECT 
          e.employeeId, e.name, e.email, e.internalNumber,
          d.teamName, d.departmentName,
          p.positionName, p.positionLevel
        FROM Employees e
        LEFT JOIN Department d ON e.departmentId = d.departmentId
        LEFT JOIN Positions p ON e.positionId = p.positionId
        ${whereSql}`;
        
      const [rows] = await pool.query(query, queryParams);
      return buildResponse(200, { employees: rows });
    }

    // ===== CASE B: 특정 직원 상세 조회 =====
    const isViewingSelf = requesterId.toString() === targetId;

    if (isViewingSelf) {
      const details = await fetchFullEmployeeDetails(pool, targetId);
      return details
        ? buildResponse(200, { employee: details })
        : buildResponse(404, { error: 'Employee not found.' });
    }

    if (isHRManager) {
      const [rows] = await pool.query(
        `SELECT 
          e.employeeId, e.name, e.email, e.internalNumber,
          d.teamName, p.positionName
        FROM Employees e
        LEFT JOIN Department d ON e.departmentId = d.departmentId
        LEFT JOIN Positions p ON e.positionId = p.positionId
        WHERE e.employeeId = ?`,
        [targetId]
      );
      if (rows.length === 0) {
        return buildResponse(404, { error: 'Employee not found.' });
      }
      return buildResponse(200, { employee: rows[0] });
    }

    if (isTeamManager) {
      const [targetRows] = await pool.query(
        'SELECT departmentId FROM Employees WHERE employeeId = ?',
        [targetId]
      );
      if (targetRows.length === 0) {
        return buildResponse(404, { error: 'Employee not found.' });
      }
      const targetDeptId = targetRows[0].departmentId;

      if (requesterDeptId === targetDeptId) {
        const details = await fetchFullEmployeeDetails(pool, targetId);
        return buildResponse(200, { employee: details });
      } else {
        return buildResponse(403, { 
          error: 'Forbidden: You can only view employees in your own department.' 
        });
      }
    }

    return buildResponse(403, { 
      error: 'Forbidden: You can only view your own information.' 
    });

  } catch (err) {
    console.error('GetEmployeeHandler Error:', err.message, err.stack);
    return buildResponse(500, { error: `Internal error: ${err.message}` });
  }
};