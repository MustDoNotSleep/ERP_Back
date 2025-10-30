package com.erp.repository;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.Employee;
import com.erp.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, String> {
    List<AppointmentRequest> findByEmployee(Employee employee);
    List<AppointmentRequest> findByStatus(ApplicationStatus status);
    List<AppointmentRequest> findByEmployeeAndCreatedAtBetween(
        Employee employee, LocalDateTime start, LocalDateTime end);
}