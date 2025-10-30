-- V1.3__salary_post_tables.sql

-- 1. Salary Records Table
CREATE TABLE salary (
    salaryId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    paymentDate VARCHAR(7) NOT NULL,
    baseSalary DECIMAL(15,2) NOT NULL,
    overtime DECIMAL(15,2),
    bonus DECIMAL(15,2),
    mealAllowance DECIMAL(15,2),
    transportAllowance DECIMAL(15,2),
    incomeTax DECIMAL(15,2),
    nationalPension DECIMAL(15,2),
    healthInsurance DECIMAL(15,2),
    employmentInsurance DECIMAL(15,2),
    total_salary DECIMAL(15,2) NOT NULL,
    net_salary DECIMAL(15,2) NOT NULL,
    status ENUM('초안', '확정', '지급완료') NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 2. Posts Table
CREATE TABLE posts (
    postId VARCHAR(36) PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category ENUM('공지', '소식', '이벤트', '일반') NOT NULL,
    viewCount INT DEFAULT 0,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- 3. Post Attachments Table
CREATE TABLE post_attachments (
    postId VARCHAR(36) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (postId) REFERENCES posts(postId)
);

-- 5. Indexes
CREATE INDEX idx_salary_employee ON salary(employeeId);
CREATE INDEX idx_salary_date ON salary(paymentDate);
CREATE INDEX idx_post_employee ON posts(employeeId);
CREATE INDEX idx_post_created ON posts(createdAt);
CREATE INDEX idx_post_category ON posts(category);