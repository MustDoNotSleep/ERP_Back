// handlers/updateEmployeeHandler.js
import { getPool } from '../config/dbConfig.js';
import { buildResponse } from '../utils/loginresponse.js';

export const updateEmployeeHandler = async (event) => {
  console.log("✏️ Update Employee Info:", JSON.stringify(event, null, 2));
  
  try {
    const body = JSON.parse(event.body || '{}');
    const { 
      employeeId,
      phoneNumber,
      address,
      internalNumber,
      bankName,
      account
    } = body;

    // 입력값 검증
    if (!employeeId) {
      return buildResponse(400, { error: 'Employee ID is required.' });
    }

    const pool = getPool();

    // 수정 가능한 필드만 업데이트
    const updateFields = [];
    const updateValues = [];

    if (phoneNumber !== undefined) {
      updateFields.push('phoneNumber = ?');
      updateValues.push(phoneNumber);
    }
    if (address !== undefined) {
      updateFields.push('address = ?');
      updateValues.push(address);
    }
    if (internalNumber !== undefined) {
      updateFields.push('internalNumber = ?');
      updateValues.push(internalNumber);
    }
    if (bankName !== undefined) {
      updateFields.push('bankName = ?');
      updateValues.push(bankName);
    }
    if (account !== undefined) {
      updateFields.push('account = ?');
      updateValues.push(account);
    }

    if (updateFields.length === 0) {
      return buildResponse(400, { error: 'No fields to update.' });
    }

    updateValues.push(employeeId);

    const query = `UPDATE Employees SET ${updateFields.join(', ')} WHERE employeeId = ?`;
    
    const [result] = await pool.query(query, updateValues);

    if (result.affectedRows === 0) {
      return buildResponse(404, { error: 'Employee not found.' });
    }

    return buildResponse(200, {
      message: 'Employee info updated successfully',
      updatedFields: updateFields.length
    });

  } catch (err) {
    console.error('UpdateEmployeeHandler Error:', err.message);
    console.error('Stack:', err.stack);
    return buildResponse(500, { error: `Internal error: ${err.message}` });
  }
};