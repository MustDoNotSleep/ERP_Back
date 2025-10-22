// --------------------------------------------------
// educationService.js
// --------------------------------------------------
import { getPool } from '../config/dbConfig.js';

/**
 * --------------------------------------------------
 * 1️⃣ 특정 직원의 학력 목록 조회
 * (GET /employees/{employeeId}/education)
 * --------------------------------------------------
 */
export async function listEducationByEmployee(employeeId) {
  if (!employeeId) {
    return {
      statusCode: 400,
      body: JSON.stringify({ message: "조회할 employeeId가 없습니다." })
    };
  }

  const pool = getPool();
  const connection = await pool.getConnection();

  try {
    const sql = `
      SELECT 
        educationId,
        employeeId,
        schoolName,
        major,
        admissionDate,
        graduationDate,
        degree,
        graduationStatus
      FROM Education
      WHERE employeeId = ?
      ORDER BY admissionDate DESC;
    `;

    const [rows] = await connection.query(sql, [employeeId]);

    return {
      statusCode: 200,
      body: JSON.stringify(rows)
    };

  } catch (error) {
    console.error("❌ Error listing education by employee:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "DB 조회 중 오류 발생", error: error.message })
    };
  } finally {
    connection.release();
  }
}

/**
 * --------------------------------------------------
 * 2️⃣ 전체 학력 목록 조회 (인사팀 전용)
 * (GET /education)
 * --------------------------------------------------
 */
export async function listAllEducation(queryParams) {
  const pool = getPool();
  const connection = await pool.getConnection();

  try {
    let sql = `
      SELECT 
        e.educationId,
        e.employeeId,
        emp.name AS employeeName,
        d.departmentName,
        d.teamName,
        e.schoolName,
        e.major,
        e.admissionDate,
        e.graduationDate,
        e.degree,
        e.graduationStatus
      FROM Education e
      JOIN Employees emp ON e.employeeId = emp.employeeId
      LEFT JOIN Department d ON emp.departmentId = d.departmentId
      WHERE 1=1
    `;
    
    const params = [];

    if (queryParams?.name) {
      sql += " AND emp.name LIKE ?";
      params.push(`%${queryParams.name}%`);
    }

    if (queryParams?.employeeId) {
      sql += " AND e.employeeId = ?";
      params.push(queryParams.employeeId);
    }

    if (queryParams?.departmentName) {
      sql += " AND d.teamName LIKE ?";
      params.push(`%${queryParams.departmentName}%`);
    }

    sql += " ORDER BY e.admissionDate DESC";

    const [rows] = await connection.query(sql, params);

    return {
      statusCode: 200,
      body: JSON.stringify(rows)
    };

  } catch (error) {
    console.error("❌ Error listing all education:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "DB 조회 오류", error: error.message })
    };
  } finally {
    connection.release();
  }
}

/**
 * --------------------------------------------------
 * 3️⃣ 학력 등록 (POST /employees/{employeeId}/education)
 * 인사팀 전용
 * --------------------------------------------------
 */
export async function createEducation(employeeId, data) {
  const pool = getPool();
  const connection = await pool.getConnection();

  try {
    if (!employeeId) {
      return {
        statusCode: 400,
        body: JSON.stringify({ message: "등록할 employeeId가 없습니다." })
      };
    }

    const {
      schoolName,
      major,
      admissionDate,
      graduationDate,
      degree,
      graduationStatus
    } = data;

    const sql = `
      INSERT INTO Education (
        employeeId, schoolName, major, admissionDate, graduationDate, degree, graduationStatus
      ) VALUES (?, ?, ?, ?, ?, ?, ?)
    `;

    await connection.query(sql, [
      employeeId,
      schoolName,
      major,
      admissionDate,
      graduationDate,
      degree,
      graduationStatus
    ]);

    return {
      statusCode: 200,
      body: JSON.stringify({ message: "✅ 학력 정보가 성공적으로 등록되었습니다." })
    };

  } catch (error) {
    console.error("❌ Error creating education:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "DB 등록 오류", error: error.message })
    };
  } finally {
    connection.release();
  }
}

/**
 * --------------------------------------------------
 * 4️⃣ 학력 수정 (PATCH /education/{educationId})
 * 인사팀 전용
 * --------------------------------------------------
 */
export async function updateEducation(educationId, data) {
  const pool = getPool();
  const connection = await pool.getConnection();

  try {
    const fields = [];
    const params = [];

    for (const [key, value] of Object.entries(data)) {
      fields.push(`${key} = ?`);
      params.push(value);
    }

    params.push(educationId);

    const sql = `UPDATE Education SET ${fields.join(", ")} WHERE educationId = ?`;
    await connection.query(sql, params);

    return {
      statusCode: 200,
      body: JSON.stringify({ message: "✅ 학력 정보가 수정되었습니다." })
    };

  } catch (error) {
    console.error("❌ Error updating education:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "DB 수정 오류", error: error.message })
    };
  } finally {
    connection.release();
  }
}

/**
 * --------------------------------------------------
 * 5️⃣ 학력 삭제 (DELETE /education/{educationId})
 * 인사팀 전용
 * --------------------------------------------------
 */
export async function deleteEducation(educationId) {
  const pool = getPool();
  const connection = await pool.getConnection();

  try {
    const sql = `DELETE FROM Education WHERE educationId = ?`;
    await connection.query(sql, [educationId]);

    return {
      statusCode: 200,
      body: JSON.stringify({ message: "✅ 학력 정보가 삭제되었습니다." })
    };

  } catch (error) {
    console.error("❌ Error deleting education:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "DB 삭제 오류", error: error.message })
    };
  } finally {
    connection.release();
  }
}
