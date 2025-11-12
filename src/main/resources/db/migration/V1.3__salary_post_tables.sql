-- V1.3: Salary, SalaryInfo 테이블 생성

-- 1. SalaryInfo 테이블
CREATE TABLE SalaryInfo (
    salaryInfoId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    bankName VARCHAR(50),
    accountNumber VARCHAR(50),
    monthlyBaseSalary DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. salary 테이블
CREATE TABLE salary (
    salaryId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT,
    paymentDate VARCHAR(7) NOT NULL,
    baseSalary DECIMAL(15,2) NOT NULL,
    overtimeAllowance DECIMAL(15,2),
    nightAllowance DECIMAL(15,2),
    dutyAllowance DECIMAL(15,2),
    bonus DECIMAL(15,2),
    incomeTax DECIMAL(15,2),
    nationalPension DECIMAL(15,2),
    healthInsurance DECIMAL(15,2),
    employmentInsurance DECIMAL(15,2),
    societyFee DECIMAL(15,2),
    advancePayment DECIMAL(15,2),
    otherDeductions DECIMAL(15,2),
    total_salary DECIMAL(15,2) NOT NULL,
    net_salary DECIMAL(15,2) NOT NULL,
    salaryStatus VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_salaryInfo_employee ON SalaryInfo(employeeId);
CREATE INDEX idx_salary_employee ON salary(employeeId);
CREATE INDEX idx_salary_paymentDate ON salary(paymentDate);
CREATE INDEX idx_salary_status ON salary(salaryStatus);