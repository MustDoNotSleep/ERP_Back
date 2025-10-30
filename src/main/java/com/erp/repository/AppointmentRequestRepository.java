package com.erp.repository;

import com.erp.entity.AppointmentRequest;
import com.erp.entity.Employee;
import com.erp.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {
    List<AppointmentRequest> findByTargetEmployee(Employee employee);
    List<AppointmentRequest> findByRequestingEmployee(Employee employee);
    List<AppointmentRequest> findByStatus(RequestStatus status);
    List<AppointmentRequest> findByTargetEmployeeAndRequestDateBetween(
        Employee employee, LocalDateTime start, LocalDateTime end);
}