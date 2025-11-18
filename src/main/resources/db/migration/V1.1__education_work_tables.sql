-- V1.1: Education, WorkExperience 테이블 생성

-- 1. education 테이블
CREATE TABLE education (
    educationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    schoolName VARCHAR(255),
    major VARCHAR(255),
    degree VARCHAR(50),
    graduationStatus VARCHAR(50),
    admissionDate DATE,
    graduationDate DATE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. workExperience 테이블
CREATE TABLE workExperience (
    experienceId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    companyName VARCHAR(255),
    jobTitle VARCHAR(255),
    finalPosition VARCHAR(255),
    finalSalary INT,
    startDate DATE,
    endDate DATE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_education_employee ON education(employeeId);
CREATE INDEX idx_workExperience_employee ON workExperience(employeeId);