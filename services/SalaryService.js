// // 💡 MySQL 클라이언트를 불러옵니다.
// const { executeQuery } = require('../utils/db/mysqlClient'); 

// /**
//  * MySQL Salary 테이블에 급여 정보를 저장합니다.
//  */
// const createSalaryRecord = async (data) => {
//     // 1. 데이터 추출 및 유효성 검사 (JSON Key는 CamelCase)
//     const { 
//         employeeId, payDate, baseSalary, allowance, bonus, 
//         incomeTax, residentTax, socialIns, 
//     } = data;

//     // 2. 총액 및 공제액 계산 (간단한 비즈니스 로직)
//     const totalPay = baseSalary + allowance + bonus;
//     const totalDeductions = incomeTax + residentTax + socialIns;
//     const netPay = totalPay - totalDeductions;

//     // 3. MySQL INSERT 쿼리 작성 (컬럼명은 DB 표준인 CamelCase 사용)
//     const sql = `
//         INSERT INTO Salary (
//             employeeId, payDate, baseSalary, allowance, bonus, totalPay, 
//             incomeTax, residentTax, socialIns, totalDeductions, netPay
//         ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
//     `;

//     const params = [
//         employeeId, payDate, baseSalary, allowance, bonus, totalPay, 
//         incomeTax, residentTax, socialIns, totalDeductions, netPay
//     ];

//     // 4. 쿼리 실행
//     await executeQuery(sql, params);

//     return {
//         message: `사번 ${employeeId}의 급여 정보가 성공적으로 저장되었습니다.`,
//         netPay: netPay 
//     };
// };

// module.exports = {
//     createSalaryRecord,
// };