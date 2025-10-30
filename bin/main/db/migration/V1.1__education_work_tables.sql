-- V1.1__education_work_tables.sql

-- 1. Education Table
CREATE TABLE education (
    educationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    schoolName VARCHAR(100) NOT NULL,
    major VARCHAR(100),
    admissionDate DATE,
    graduationDate DATE,
    degree ENUM('고졸', '전문학사', '학사', '석사', '박사') NOT NULL,
    graduationStatus ENUM('재학', '졸업', '휴학', '중퇴', '졸업예정') NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 2. Work Experience Table
CREATE TABLE workExperience (
    experienceId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    companyName VARCHAR(100),
    jobTitle VARCHAR(100) ,
    finalPosition VARCHAR(100),
    finalSalary INT,
    startDate DATE,
    endDate DATE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 3. Certificates Table
CREATE TABLE certificates (
    certificateId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    certificateName VARCHAR(100),
    issuingAuthority VARCHAR(100),
    score VARCHAR(50),
    acquisitionDate DATE,
    expirationDate DATE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 4. Indexes
CREATE INDEX idx_education_employee ON education(employeeId);
CREATE INDEX idx_experience_employee ON work_experience(employeeId);
CREATE INDEX idx_certificate_employee ON certificates(employeeId);