-- V1.4__security_updates.sql

-- 1. Create encryption key management table
CREATE TABLE encryption_keys (
    keyId BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyType VARCHAR(50) NOT NULL,
    keyValue VARBINARY(255) NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Create stored procedure for password updates
DELIMITER //
CREATE PROCEDURE update_password_to_bcrypt()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE empId INT;
    DECLARE plainPass VARCHAR(100);
    DECLARE cur CURSOR FOR SELECT employeeId, password FROM employees;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO empId, plainPass;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Skip already hashed passwords
        IF plainPass NOT LIKE '$2a$%' THEN
            -- Update will be handled by application script
            UPDATE employees 
            SET password = CONCAT('NEEDS_HASH:', plainPass) 
            WHERE employeeId = empId;
        END IF;
    END LOOP;
    
    CLOSE cur;
END //
DELIMITER ;

-- 3. Create stored procedure for RRN encryption
DELIMITER //
CREATE PROCEDURE prepare_rrn_encryption()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE empId INT;
    DECLARE plainRrn VARCHAR(20);
    DECLARE cur CURSOR FOR SELECT employeeId, rrn FROM employees;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO empId, plainRrn;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Mark for encryption by application
        IF plainRrn NOT LIKE 'ENC:%' THEN
            UPDATE employees 
            SET rrn = CONCAT('NEEDS_ENC:', plainRrn) 
            WHERE employeeId = empId;
        END IF;
    END LOOP;
    
    CLOSE cur;
END //
DELIMITER ;

-- 4. Execute procedures
CALL update_password_to_bcrypt();
CALL prepare_rrn_encryption();

-- 5. Drop procedures after use
DROP PROCEDURE update_password_to_bcrypt;
DROP PROCEDURE prepare_rrn_encryption;