// erp.js
import { loginHandler } from './handler/loginHandler.js';
import { getEmployeeHandler } from './handler/getEmployeeHandler.js';
import { updateEmployeeHandler } from './handler/updateEmployeeHandler.js';
import { changePasswordHandler } from './handler/changePasswordHandler.js';
import { registerEmployeeHandler } from './handler/registerEmployeeHandler.js';
import { buildResponse } from './utils/loginresponse.js';

export const handler = async (event) => {
  console.log("🚀 ERP Lambda Invoked:", event.httpMethod, event.path);

  try {
    const httpMethod = event.httpMethod;
    const path = event.path;

    // CORS preflight 요청 처리
    if (httpMethod === 'OPTIONS') {
      return buildResponse(200, { message: 'CORS preflight successful' });
    }
    
    // --- RESTful 라우팅 로직 ---

    // POST /login -> 로그인 처리
    if (httpMethod === 'POST' && path === '/login') {
      console.log("🧩 라우팅: loginHandler");
      return await loginHandler(event);
    }

    if (httpMethod === 'POST' && path === '/register'){
      console.log("🧩 라우팅: registerEmployeeHandler");
      return await registerEmployeeHandler(event);
    }

    // GET /employees -> 전체 직원 목록 조회
    if (httpMethod === 'GET' && path === '/employees') {
      console.log("🧩 라우팅: getEmployeeHandler (전체 목록)");
      return await getEmployeeHandler(event);
    }

    // GET /employees/{employeeId} -> 특정 직원 정보 조회
    if (httpMethod === 'GET' && /^\/employees\/[^\/]+$/.test(path)) {
      console.log("🧩 라우팅: getEmployeeHandler (특정 직원)");
      return await getEmployeeHandler(event);
    }
    
    // PUT /employees/{employeeId} -> 직원 정보 수정
    if (httpMethod === 'PUT' && /^\/employees\/[^\/]+$/.test(path)) {
      console.log("🧩 라우팅: updateEmployeeHandler");
      return await updateEmployeeHandler(event);
    }

    // Patch /employees/{employeeId}/password -> 비밀번호 변경
    if (httpMethod === 'PATCH' && /^\/employees\/[^\/]+\/password$/.test(path)) {
      console.log("🧩 라우팅: changePasswordHandler");
      return await changePasswordHandler(event);
    }

    // --- 일치하는 경로가 없을 경우 404 Not Found 응답 ---
    console.warn("⚠️ 404 Not Found:", httpMethod, path);
    return buildResponse(404, { 
      error: 'Not Found: The requested route does not exist.',
      requestedPath: path,
      requestedMethod: httpMethod
    });

  } catch (error) {
    console.error("❌ ERP Lambda Error:", error);
    return buildResponse(500, { 
      error: 'Internal Server Error',
      message: error.message 
    });
  }
};