-- V1.5__additional_tables.sql

-- Military Service Info Table
CREATE TABLE MilitaryServiceInfo (
    militaryInfoId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    militaryStatus ENUM('만기전역', '면제', '해당없음', '복무중') NOT NULL,
    militaryBranch ENUM('육군', '해군', '공군', '해병', '의경', '해당없음'),
    militaryRank ENUM('이병', '일병', '상병', '병장', '하사', '중사', '상사'),
    militarySpecialty ENUM('보병', '포병', '통신', '공병', '기타'),
    exemptionReason ENUM('복무대기', '생계곤란', '질병', '기타'),
    serviceStartDate DATE,
    serviceEndDate DATE,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- Course Table
CREATE TABLE Courses (
    courseId BIGINT AUTO_INCREMENT PRIMARY KEY,
    courseName VARCHAR(255),
    completionCriteria VARCHAR(255),
    capacity INT,
    courseType ENUM('필수이수', '선택이수',),
    startDate DATE,
    endDate DATE,
    objective TEXT,
    creator BIGINT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (creator) REFERENCES employees(employeeId)
);

-- Course Application Table
CREATE TABLE CourseApplications (
    applicationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    courseId BIGINT NOT NULL,
    employeeId BIGINT NOT NULL,
    applicationDate DATETIME,
    status ENUM('대기', '승인', '반려', '취소') NOT NULL,
    processedBy BIGINT,
    processedAt DATETIME,
    rejectionReason VARCHAR(500),
    FOREIGN KEY (courseId) REFERENCES Courses(courseId),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (processedBy) REFERENCES employees(employeeId)
);

-- Appointment Request Table
CREATE TABLE AppointmentRequests (
    appointmentRequestId BIGINT AUTO_INCREMENT PRIMARY KEY,
    targetEmployeeId BIGINT NOT NULL,
    requestingEmployeeId BIGINT NOT NULL,
    appointmentType ENUM('승진', '발령', '퇴직', '징계') NOT NULL,
    newDepartmentId BIGINT,
    effectiveStartDate DATE,
    effectiveEndDate DATE,
    reason TEXT,
    status ENUM('대기', '승인', '반려', '취소') NOT NULL,
    approverId BIGINT,
    requestDate DATETIME,
    processedDate DATETIME,
    FOREIGN KEY (targetEmployeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (requestingEmployeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (newDepartmentId) REFERENCES departments(departmentId),
    FOREIGN KEY (approverId) REFERENCES employees(employeeId)
);

-- Document Application Table
CREATE TABLE DocumentApplications (
    documentId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    documentType ENUM('재직증명서', '경력증명서', '급여증명서', '근로소득증명서', '휴가확인서', '기타') NOT NULL,
    purpose VARCHAR(100),
    language ENUM('한국어', '영어') NOT NULL,
    reason TEXT,
    documentStatus ENUM('대기', '승인', '반려', '발급완료') NOT NULL,
    applicationDate DATETIME,
    processedBy BIGINT,
    processedAt DATETIME,
    rejectionReason VARCHAR(500),
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    createdBy VARCHAR(100),
    updatedBy VARCHAR(100),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId),
    FOREIGN KEY (processedBy) REFERENCES employees(employeeId)
);

-- Document Issued Files Table (여러 발급 파일 저장)
CREATE TABLE document_issued_files (
    documentId BIGINT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (documentId) REFERENCES DocumentApplications(documentId)
);

-- Salary Info Table
CREATE TABLE SalaryInfo (
    salaryInfoId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    bankName ENUM('국민', '신한', '우리', '하나', '농협', '기업', 'SC', '시티', '케이뱅크', '카카오', '토스'),
    accountNumber VARCHAR(50),
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId)
);

-- Create Indexes
CREATE INDEX idx_military_employee ON MilitaryServiceInfo(employeeId);
CREATE INDEX idx_course_dates ON Courses(startDate, endDate);
CREATE INDEX idx_course_type ON Courses(courseType);
CREATE INDEX idx_course_app_employee ON CourseApplications(employeeId);
CREATE INDEX idx_course_app_status ON CourseApplications(status);
CREATE INDEX idx_appointment_employee ON AppointmentRequests(targetEmployeeId);
CREATE INDEX idx_appointment_status ON AppointmentRequests(status);
CREATE INDEX idx_doc_app_employee ON DocumentApplications(employeeId);
CREATE INDEX idx_doc_app_status ON DocumentApplications(documentStatus);
CREATE INDEX idx_salary_info_employee ON SalaryInfo(employeeId);