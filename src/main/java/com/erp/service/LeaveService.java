// package com.erp.service;

// import com.erp.dto.LeaveDto;
// import com.erp.entity.Employee;
// import com.erp.entity.Leave;
// import com.erp.exception.BusinessException;
// import com.erp.exception.EntityNotFoundException;
// import com.erp.repository.EmployeeRepository;
// import com.erp.repository.LeaveRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.IDENTITY;
// import java.util.stream.Collectors;

// @Service
// @Transactional(readOnly = true)
// @RequiredArgsConstructor
// public class LeaveService {
    
//     private final LeaveRepository leaveRepository;
//     private final EmployeeRepository employeeRepository;
    
//     @Transactional
//     public String requestLeave(LeaveDto.Request request) {
//         Employee employee = findEmployee(request.getEmployeeId());
//         validateLeaveRequest(employee, request.getStartDate(), request.getEndDate());
        
//         Leave leave = Leave.builder()
//             .id(IDENTITY.randomIDENTITY().toString())
//             .employee(employee)
//             .type(request.getType())
//             .startDate(request.getStartDate())
//             .endDate(request.getEndDate())
//             .reason(request.getReason())
//             .status(Leave.LeaveStatus.PENDING)
//             .build();
        
//         leaveRepository.save(leave);
//         return leave.getId();
//     }
    
//     @Transactional
//     public void processLeaveRequest(String leaveId, LeaveDto.ApprovalRequest request) {
//         Leave leave = findLeave(leaveId);
//         Employee approver = findEmployee(request.getApproverId());
        
//         if (leave.getStatus() != Leave.LeaveStatus.PENDING) {
//             throw new BusinessException("Leave request is not in PENDING status", "INVALID_STATUS");
//         }
        
//         if (request.isApproved()) {
//             leave.approve(approver);
//         } else {
//             leave.reject(approver);
//         }
//     }
    
//     @Transactional
//     public void cancelLeave(String leaveId) {
//         Leave leave = findLeave(leaveId);
        
//         if (leave.getStatus() != Leave.LeaveStatus.PENDING 
//             && leave.getStatus() != Leave.LeaveStatus.APPROVED) {
//             throw new BusinessException("Cannot cancel leave in current status", "INVALID_STATUS");
//         }
        
//         if (leave.getStartDate().isBefore(LocalDate.now())) {
//             throw new BusinessException("Cannot cancel past leave", "PAST_LEAVE");
//         }
        
//         leave.cancel();
//     }
    
//     public LeaveDto.Response getLeave(String id) {
//         return leaveRepository.findById(id)
//             .map(LeaveDto.Response::from)
//             .orElseThrow(() -> new EntityNotFoundException("Leave", id));
//     }
    
//     public List<LeaveDto.Response> getEmployeeLeaves(String employeeId) {
//         Employee employee = findEmployee(employeeId);
//         return leaveRepository.findByEmployee(employee).stream()
//             .map(LeaveDto.Response::from)
//             .collect(Collectors.toList());
//     }
    
//     public List<LeaveDto.Response> getPendingLeaves() {
//         return leaveRepository.findByStatusAndStartDateGreaterThanEqual(
//             Leave.LeaveStatus.PENDING, LocalDate.now()
//         ).stream()
//             .map(LeaveDto.Response::from)
//             .collect(Collectors.toList());
//     }
    
//     private Employee findEmployee(String id) {
//         return employeeRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Employee", id));
//     }
    
//     private Leave findLeave(String id) {
//         return leaveRepository.findById(id)
//             .orElseThrow(() -> new EntityNotFoundException("Leave", id));
//     }
    
//     private void validateLeaveRequest(Employee employee, LocalDate startDate, LocalDate endDate) {
//         if (startDate.isBefore(LocalDate.now())) {
//             throw new BusinessException("Leave cannot start in the past", "INVALID_DATE");
//         }
        
//         if (endDate.isBefore(startDate)) {
//             throw new BusinessException("End date must be after start date", "INVALID_DATE");
//         }
        
//         List<Leave> overlappingLeaves = leaveRepository.findApprovedLeavesInPeriod(
//             employee, startDate, endDate
//         );
        
//         if (!overlappingLeaves.isEmpty()) {
//             throw new BusinessException(
//                 "Leave period overlaps with existing approved leave",
//                 "OVERLAPPING_LEAVE"
//             );
//         }
//     }
// }