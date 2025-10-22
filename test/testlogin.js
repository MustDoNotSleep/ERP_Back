// testLogin.js
// 단독 실행용 - VSCode에서 Node.js로 실행 가능

import { loginHandler } from '../handler/loginHandler.js';

// ✅ 테스트용 이벤트 객체 생성
const testEvent = {
  body: JSON.stringify({
    email: 'parkin12@apex.com',   // 실제 DB에 있는 이메일로 변경하세요
    password: 'password1234'             // 실제 DB 비밀번호로 변경
  }),
};

const runTest = async () => {
  try {
    console.log('🚀 로그인 핸들러 테스트 시작...\n');
    
    // Lambda 핸들러 호출 방식 그대로 실행
    const response = await loginHandler(testEvent);

    console.log('✅ Lambda 결과:\n', response);
    console.log('\n📦 반환된 Body(JSON):\n', JSON.parse(response.body));
  } catch (err) {
    console.error('❌ 테스트 중 오류 발생:', err);
  }
};

runTest();
