-- V1.0__init_schema.sql

-- 1. Department Table
CREATE TABLE departments (
    departmentId BIGINT AUTO_INCREMENT PRIMARY KEY,
    departmentName VARCHAR(100) NOT NULL,
    teamName VARCHAR(100) NOT NULL,
    manage BOOLEAN NOT NULL DEFAULT FALSE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100)
);

-- 2. Position Table
CREATE TABLE positions (
    positionId BIGINT AUTO_INCREMENT PRIMARY KEY,
    positionName VARCHAR(100) NOT NULL,
    positionLevel INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100)
);

-- 3. Employee Table
CREATE TABLE employees (
    employeeId BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    nameEng VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    phone VARCHAR(20),
    birthDate DATE,
    hireDate DATE NOT NULL,
    rrn VARCHAR(255) NOT NULL,  -- Encrypted
    address VARCHAR(255),
    addressDetails VARCHAR(255),
    familyCertificate VARCHAR(255),
    employmentType ENUM('정규직', '계약직', '인턴', '알바') NOT NULL,
    internalNumber VARCHAR(20),
    nationality VARCHAR(20) NOT NULL DEFAULT '내국인',
    quitDate DATE,
    departmentId BIGINT,
    positionId BIGINT,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (departmentId) REFERENCES departments(departmentId),
    FOREIGN KEY (positionId) REFERENCES positions(positionId)
);

-- 4. Indexes
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_name ON employees(name);
CREATE INDEX idx_employee_department ON employees(departmentId);
CREATE INDEX idx_employee_position ON employees(positionId);
CREATE INDEX idx_department_name ON departments(departmentName);
CREATE INDEX idx_position_level ON positions(positionLevel);