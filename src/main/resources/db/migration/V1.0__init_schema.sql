-- V1.0: 초기 스키마 생성 (Department, Position, Employee, MilitaryServiceInfo, Certificate)

-- 1. departments 테이블
CREATE TABLE departments (
    departmentId BIGINT AUTO_INCREMENT PRIMARY KEY,
    departmentName VARCHAR(255) NOT NULL,
    teamName VARCHAR(255),
    manage BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. positions 테이블
CREATE TABLE positions (
    positionId BIGINT AUTO_INCREMENT PRIMARY KEY,
    positionName VARCHAR(255) NOT NULL,
    positionLevel INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. employees 테이블
CREATE TABLE employees (
    employeeId BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nameEng VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rrn VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(500),
    addressDetails VARCHAR(500),
    phone VARCHAR(50),
    birthDate DATE,
    hireDate DATE,
    quitDate DATE,
    internalNumber VARCHAR(50),
    familyCertificate VARCHAR(500),
    departmentId BIGINT,
    positionId BIGINT,
    employmentType VARCHAR(50) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (departmentId) REFERENCES departments(departmentId),
    FOREIGN KEY (positionId) REFERENCES positions(positionId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. MilitaryServiceInfo 테이블
CREATE TABLE MilitaryServiceInfo (
    militaryInfoId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    militaryStatus VARCHAR(50) NOT NULL,
    militaryBranch VARCHAR(50),
    militaryRank VARCHAR(50),
    militarySpecialty VARCHAR(50),
    exemptionReason VARCHAR(50),
    serviceStartDate DATE,
    serviceEndDate DATE,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. certificates 테이블
CREATE TABLE certificates (
    certificateId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    certificateName VARCHAR(255),
    issuingAuthority VARCHAR(255),
    expirationDate DATE,
    acquisitionDate DATE,
    score VARCHAR(50),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_department ON employees(departmentId);
CREATE INDEX idx_employee_position ON employees(positionId);
CREATE INDEX idx_military_employee ON MilitaryServiceInfo(employeeId);
CREATE INDEX idx_certificate_employee ON certificates(employeeId);