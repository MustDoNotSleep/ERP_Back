const { RDSDataClient, ExecuteStatementCommand } = require('@aws-sdk/client-rds-data');

// 데이터베이스 연결 설정은 환경 변수를 통해 가져옵니다.
const rdsDataClient = new RDSDataClient({ region: process.env.REGION });
const databaseName = process.env.DATABASE_NAME;
const resourceArn = process.env.RESOURCE_ARN;
const secretArn = process.env.SECRET_ARN;

exports.handler = async (event) => {
    let body;

    try {
        // HTTP 요청 본문을 JSON 객체로 파싱합니다.
        body = JSON.parse(event.body);
    } catch (e) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: 'Invalid JSON format in request body' })
        };
    }

    // 필수 필드들을 정의합니다.
    const requiredFields = [
        'employeeId', 'name', 'password', 'rrn', 'address', 'phoneNumber',
        'email', 'birthDate', 'hireDate', 'bankName', 'account', 'department_id',
        'position_id'
    ];
    
    // 필수 필드 누락 여부를 체크합니다.
    const missingFields = requiredFields.filter(field => body[field] === undefined);

    if (missingFields.length > 0) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: `Missing required fields: ${missingFields.join(', ')}` })
        };
    }

    const {
        employeeId, name, password, rrn, address, phoneNumber,
        email, birthDate, hireDate, bankName, account, quitDate,
        internalNumber, department_id, position_id
    } = body;
    
    // SQL INSERT 쿼리를 작성합니다. 모든 컬럼이 포함되어야 합니다.
    const sql = `
        INSERT INTO employees (
            employeeId, name, password, rrn, address, phoneNumber, email,
            birthDate, hireDate, bankName, account, quitDate, internalNumber,
            department_id, position_id
        ) VALUES (
            :employeeId, :name, :password, :rrn, :address, :phoneNumber, :email,
            :birthDate, :hireDate, :bankName, :account, :quitDate, :internalNumber,
            :department_id, :position_id
        );
    `;

    // SQL 쿼리에 바인딩할 파라미터를 정의합니다.
    const params = [
        { name: 'employeeId', value: { longValue: employeeId } },
        { name: 'name', value: { stringValue: name } },
        { name: 'password', value: { stringValue: password } },
        { name: 'rrn', value: { stringValue: rrn } },
        { name: 'address', value: { stringValue: address } },
        { name: 'phoneNumber', value: { stringValue: phoneNumber } },
        { name: 'email', value: { stringValue: email } },
        { name: 'birthDate', value: { stringValue: birthDate } },
        { name: 'hireDate', value: { stringValue: hireDate } },
        { name: 'bankName', value: { stringValue: bankName } },
        { name: 'account', value: { stringValue: account } },
        { name: 'quitDate', value: { stringValue: quitDate || null } },
        { name: 'internalNumber', value: { stringValue: internalNumber || null } },
        { name: 'department_id', value: { longValue: department_id } },
        { name: 'position_id', value: { longValue: position_id } }
    ];

    const command = new ExecuteStatementCommand({
        sql: sql,
        resourceArn: resourceArn,
        secretArn: secretArn,
        database: databaseName,
        parameters: params,
        continueAfterTimeout: true 
    });

    try {
        await rdsDataClient.send(command);

        return {
            statusCode: 201,
            body: JSON.stringify({ message: 'Employee created successfully' })
        };
    } catch (error) {
        console.error('Error inserting employee:', error);
        
        return {
            statusCode: 500,
            body: JSON.stringify({ message: 'Failed to create employee', error: error.message })
        };
    }
};