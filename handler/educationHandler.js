// --------------------------------------------------
// educationHandler.js
// --------------------------------------------------
import {
  listEducationByEmployee,
  listAllEducation,
  createEducation,
  updateEducation,
  deleteEducation
} from '../services/educationService.js';

// --- 권한 설정 ---
const MANAGER_LEVEL = 6;
const HR_TEAM_NAME = '인사팀';

/**
 * --------------------------------------------------
 * 학력 관리 핸들러 (메인 라우터)
 * 'educationHandler.handler'로 호출됩니다.
 * --------------------------------------------------
 */
export const handler = async (event) => {
  console.log("📩 Event Received:", JSON.stringify(event));

  const httpMethod = event.httpMethod;
  const path = event.path;

  try {
    // --- 1️⃣ 인증 정보 추출 ---
    const claims = event.requestContext?.authorizer?.claims;
    const userTeam = claims?.teamName;
    const userLevel = parseInt(claims?.level || claims?.positionLevel || 0, 10);
    const userEmployeeId = claims?.employeeId;

    if (!claims || !userEmployeeId) {
      return {
        statusCode: 401,
        body: JSON.stringify({ message: "Unauthorized: No valid claims provided." }),
      };
    }

    // =====================================================
    // 2️⃣ 특정 직원 학력 조회 (본인 또는 인사팀만 가능)
    // GET /employees/{employeeId}/education
    // =====================================================
    if (httpMethod === "GET" && path.match(/^\/employees\/\d+\/education$/)) {
      const { employeeId } = event.pathParameters || {};

      // 본인 조회 or 인사팀
      if (parseInt(employeeId, 10) !== parseInt(userEmployeeId, 10)) {
        if (userTeam !== HR_TEAM_NAME || userLevel < MANAGER_LEVEL) {
          return {
            statusCode: 403,
            body: JSON.stringify({ message: "Forbidden: 해당 직원의 학력 조회 권한이 없습니다." }),
          };
        }
      }

      return await listEducationByEmployee(employeeId);
    }

    // =====================================================
    // 3️⃣ 전체 학력 조회 (인사팀만 가능)
    // GET /education
    // =====================================================
    if (httpMethod === "GET" && path === "/education") {
      if (userTeam !== HR_TEAM_NAME || userLevel < MANAGER_LEVEL) {
        return {
          statusCode: 403,
          body: JSON.stringify({ message: "Forbidden: 전체 학력 조회 권한이 없습니다." }),
        };
      }

      const queryParams = event.queryStringParameters || {};
      return await listAllEducation(queryParams);
    }

    // =====================================================
    // 4️⃣ 학력 등록 (인사팀만 가능)
    // POST /employees/{employeeId}/education
    // =====================================================
    if (httpMethod === "POST" && path.match(/^\/employees\/\d+\/education$/)) {
      if (userTeam !== HR_TEAM_NAME || userLevel < MANAGER_LEVEL) {
        return {
          statusCode: 403,
          body: JSON.stringify({ message: "Forbidden: 학력 등록 권한이 없습니다." }),
        };
      }

      const { employeeId } = event.pathParameters || {};
      const body = JSON.parse(event.body || "{}");
      return await createEducation(employeeId, body);
    }

    // =====================================================
    // 5️⃣ 학력 수정 (인사팀만 가능)
    // PATCH /education/{educationId}
    // =====================================================
    if (httpMethod === "PATCH" && path.match(/^\/education\/\d+$/)) {
      if (userTeam !== HR_TEAM_NAME || userLevel < MANAGER_LEVEL) {
        return {
          statusCode: 403,
          body: JSON.stringify({ message: "Forbidden: 학력 수정 권한이 없습니다." }),
        };
      }

      const { educationId } = event.pathParameters || {};
      const body = JSON.parse(event.body || "{}");
      return await updateEducation(educationId, body);
    }

    // =====================================================
    // 6️⃣ 학력 삭제 (인사팀만 가능)
    // DELETE /education/{educationId}
    // =====================================================
    if (httpMethod === "DELETE" && path.match(/^\/education\/\d+$/)) {
      if (userTeam !== HR_TEAM_NAME || userLevel < MANAGER_LEVEL) {
        return {
          statusCode: 403,
          body: JSON.stringify({ message: "Forbidden: 학력 삭제 권한이 없습니다." }),
        };
      }

      const { educationId } = event.pathParameters || {};
      return await deleteEducation(educationId);
    }

    // =====================================================
    // 7️⃣ 일치하는 경로 없음
    // =====================================================
    return {
      statusCode: 404,
      body: JSON.stringify({ message: "Not Found" }),
    };

  } catch (error) {
    console.error("❌ Handler Error:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "서버 내부 오류", error: error.message }),
    };
  }
};
