// handlers/registerEmployeeHandler.js
import { getPool } from '../config/dbConfig.js';
import { buildResponse } from '../utils/loginresponse.js';
import jwt from 'jsonwebtoken';

// 관리자 권한으로 인정할 직급 레벨
const MANAGER_LEVEL = 6;
// 직원 등록을 허용할 팀 이름
const HR_TEAM_NAME = '인사팀';

export const registerEmployeeHandler = async (event) => {
  console.log("📝 Register New Employee:", event.httpMethod, event.path);

  let connection = null;

  try {
    // 1. JWT 토큰 검증으로 요청자 신원 확인
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

    // 2. DB 조회로 요청자 권한 확인 (인사팀 & 관리자 레벨)
    const pool = getPool();
    const [userRows] = await pool.query(
      `SELECT d.teamName, p.positionLevel 
       FROM Employees e
       JOIN Department d ON e.departmentId = d.departmentId
       JOIN Positions p ON e.positionId = p.positionId
       WHERE e.employeeId = ?`,
      [requesterId]
    );

    if (userRows.length === 0) {
      return buildResponse(403, { error: 'Forbidden: Requester not found.' });
    }
    const requesterAuth = userRows[0];

    if (requesterAuth.teamName !== HR_TEAM_NAME || requesterAuth.positionLevel < MANAGER_LEVEL) {
      return buildResponse(403, { error: 'Forbidden: You do not have permission to register new employees.' });
    }

    // 3. 신규 직원 정보 파싱
    const body = JSON.parse(event.body || '{}');
    const { 
      // 기본정보
      employeeId,
      name, 
      nameeng, 
      email, 
      password, 
      phoneNumber, 
      //birthDate, 
      hireDate, 
      rrn, // 주민등록번호
      address,
      addressDetails,
      familyCertificate,
      
      // 회사정보
      departmentId, 
      positionId,
      employmentType,
      username,
      internalNumber,
      
      // 병역정보 (단일 객체)
      militaryInfo,
      
      // 급여정보 (단일 객체)
      salaryInfo,
      
      // 학력정보 (배열)
      educations,
      
      // 경력정보 (배열)
      workExperiences,
      
      // 자격증정보 (배열)
      certificates, 

      //내국인, 외국인 (배열)
      nationality 
    } = body;

    // 4. 필수 필드 검증
    if (!name || !email || !password || !hireDate || !departmentId || !positionId) {
      return buildResponse(400, { error: 'Bad Request: Missing required fields.' });
    }

    // 5. 이메일 중복 체크
    const [existingUser] = await pool.query(
      'SELECT employeeId FROM Employees WHERE email = ?', 
      [email]
    );
    if (existingUser.length > 0) {
      return buildResponse(409, { error: 'Conflict: Email already exists.' });
    }

    // 6. 트랜잭션 시작
    connection = await pool.getConnection();
    await connection.beginTransaction();

    try {
      // 7. Employees 테이블에 직원 기본정보 저장
      const employeeData = {
        employeeId,
        name,
        nameeng,
        email,
        password, // 평문 저장 (요구사항에 따라)
        phoneNumber,
        //birthDate,
        hireDate,
        rrn,
        address,
        addressDetails,
        familyCertificate,
        departmentId,
        positionId,
        employmentType,
        username,
        internalNumber,
        nationality
      };

      const [employeeResult] = await connection.query(
        'INSERT INTO Employees SET ?', 
        [employeeData]
      );
      const newEmployeeId = employeeData.employeeId;
      console.log(`✅ Employee created with ID: ${newEmployeeId}`);

      // 8. 병역정보 저장 (MilitaryServiceInfo)
      if (militaryInfo && Object.keys(militaryInfo).length > 0) {
        const militaryData = {
          employeeId : newEmployeeId,
          militaryStatus: militaryInfo.militaryStatus,
          militaryBranch: militaryInfo.militaryBranch,
          militaryRank: militaryInfo.militaryRank,
          militarySpecialty: militaryInfo.militarySpecialty,
          exemptionReason: militaryInfo.exemptionReason,
          serviceStartDate: militaryInfo.serviceStartDate || null,
          serviceEndDate: militaryInfo.serviceEndDate || null
        };

        await connection.query(
          'INSERT INTO MilitaryServiceInfo SET ?',
          [militaryData]
        );
        console.log(`✅ Military info saved for employee ${newEmployeeId}`);
      }

      // 9. 급여정보 저장 (SalaryInfo)
      if (salaryInfo && Object.keys(salaryInfo).length > 0) {
        const salaryData = {
          employeeId : newEmployeeId,
          bankName: salaryInfo.bankName,
          accountNumber: salaryInfo.accountNumber,
          salary: salaryInfo.salary
        };

        await connection.query(
          'INSERT INTO SalaryInfo SET ?',
          [salaryData]
        );
        console.log(`✅ Salary info saved for employee ${newEmployeeId}`);
      }

      // 10. 학력정보 저장 (Education) - 배열
      if (educations && Array.isArray(educations) && educations.length > 0) {
        for (const edu of educations) {
          const educationData = {
            employeeId : newEmployeeId,

            schoolName: edu.schoolName,
            major: edu.major,
            admissionDate: edu.admissionDate,
            graduationDate: edu.graduationDate,
            degree: edu.degree,
            graduationStatus: edu.graduationStatus
          };

          await connection.query(
            'INSERT INTO Education SET ?',
            [educationData]
          );
        }
        console.log(`✅ ${educations.length} education record(s) saved`);
      }

      // 11. 경력정보 저장 (WorkExperience) - 배열
      if (workExperiences && Array.isArray(workExperiences) && workExperiences.length > 0) {
        for (const work of workExperiences) {
          const workData = {
            employeeId : newEmployeeId,

            companyName: work.companyName,
            jobTitle: work.jobTitle,
            finalPosition: work.finalPosition,
            finalSalary: work.finalSalary,
            startDate: work.startDate,
            endDate: work.endDate
          };

          await connection.query(
            'INSERT INTO WorkExperience SET ?',
            [workData]
          );
        }
        console.log(`✅ ${workExperiences.length} work experience record(s) saved`);
      }

      // 12. 자격증정보 저장 (Certificates) - 배열
      if (certificates && Array.isArray(certificates) && certificates.length > 0) {
        for (const cert of certificates) {
          const certData = {
            employeeId : newEmployeeId,

            certificateName: cert.certificateName,
            issuingAuthority: cert.issuingAuthority,
            score: cert.score,
            acquisitionDate: cert.acquisitionDate,
            expirationDate: cert.expirationDate || null
          };

          await connection.query(
            'INSERT INTO Certificates SET ?',
            [certData]
          );
        }
        console.log(`✅ ${certificates.length} certificate(s) saved`);
      }

      // 13. 트랜잭션 커밋
      await connection.commit();
      console.log(`🎉 All data committed successfully for employee ${newEmployeeId}`);

      // 14. 성공 응답
      return buildResponse(201, {
        message: 'Employee registered successfully',
        employeeId: newEmployeeId,
      });

    } catch (insertError) {
      // 트랜잭션 롤백
      await connection.rollback();
      console.error('❌ Transaction rolled back due to error:', insertError.message);
      throw insertError;
    }

  } catch (err) {
    console.error('RegisterEmployeeHandler Error:', err.message, err.stack);
    return buildResponse(500, { error: `Internal error: ${err.message}` });
  } finally {
    // 연결 반환
    if (connection) {
      connection.release();
    }
  }
};