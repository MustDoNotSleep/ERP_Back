-- V1.4: DocumentApplication, Course, CourseApplication 테이블 생성

-- 1. document_applications 테이블
CREATE TABLE documentApplications (
    documentId BIGINT AUTO_INCREMENT PRIMARY KEY,
    employeeId BIGINT NOT NULL,
    documentType VARCHAR(50) NOT NULL,
    purpose VARCHAR(100),
    language VARCHAR(50),
    reason TEXT,
    documentStatus VARCHAR(50) NOT NULL,
    applicationDate TIMESTAMP,
    processedBy BIGINT,
    processedAt TIMESTAMP,
    rejectionReason VARCHAR(500),
    copies INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    FOREIGN KEY (processedBy) REFERENCES employees(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. document_issued_files 테이블
CREATE TABLE documentIssued_files (
    documentId BIGINT NOT NULL,
    file_url VARCHAR(255),
    FOREIGN KEY (documentId) REFERENCES documentApplications(documentId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Courses 테이블
CREATE TABLE Courses (
    courseId BIGINT AUTO_INCREMENT PRIMARY KEY,
    courseName VARCHAR(255) NOT NULL,
    completionCriteria VARCHAR(255),
    capacity INT,
    courseType VARCHAR(50) NOT NULL,
    startDate DATE,
    endDate DATE,
    objective TEXT,
    creator BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT '대기',
    approver BIGINT,
    processedDate TIMESTAMP,
    comment VARCHAR(500),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator) REFERENCES employees(employeeId),
    FOREIGN KEY (approver) REFERENCES employees(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. CourseApplications 테이블
CREATE TABLE CourseApplications (
    applicationId BIGINT AUTO_INCREMENT PRIMARY KEY,
    courseId BIGINT NOT NULL,
    employeeId BIGINT NOT NULL,
    applicationDate TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    processedBy BIGINT,
    processedAt TIMESTAMP,
    rejectionReason VARCHAR(500),
    FOREIGN KEY (courseId) REFERENCES Courses(courseId) ON DELETE CASCADE,
    FOREIGN KEY (employeeId) REFERENCES employees(employeeId) ON DELETE CASCADE,
    FOREIGN KEY (processedBy) REFERENCES employees(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_document_employee ON documentApplications(employeeId);
CREATE INDEX idx_document_status ON documentApplications(documentStatus);
CREATE INDEX idx_course_creator ON Courses(creator);
CREATE INDEX idx_course_status ON Courses(status);
CREATE INDEX idx_course_dates ON Courses(startDate, endDate);
CREATE INDEX idx_courseApp_course ON CourseApplications(courseId);
CREATE INDEX idx_courseApp_employee ON CourseApplications(employeeId);