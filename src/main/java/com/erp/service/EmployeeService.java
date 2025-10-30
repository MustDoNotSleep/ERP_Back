package com.erp.service;

import com.erp.dto.EmployeeDto;
import com.erp.dto.PageResponse;
import com.erp.entity.Department;
import com.erp.entity.Employee;
import com.erp.entity.Position;
import com.erp.exception.BusinessException;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.DepartmentRepository;
import com.erp.repository.EmployeeRepository;
import com.erp.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public String registerEmployee(EmployeeDto.Request request) {
        validateUniqueEmail(request.getEmail());
        
        Department department = findDepartment(request.getDepartmentId());
        Position position = findPosition(request.getPositionId());
        
        Employee employee = Employee.builder()
            .id(UUID.randomUUID().toString())
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .rrn(request.getRrn())
            .phone(request.getPhone())
            .address(request.getAddress())
            .birthDate(request.getBirthDate())
            .hireDate(request.getHireDate())
            .department(department)
            .position(position)
            .roles(request.getRoles())
            .build();
        
        employeeRepository.save(employee);
        return employee.getId();
    }
    
    public EmployeeDto.Response getEmployee(String id) {
        return employeeRepository.findById(id)
            .map(EmployeeDto.Response::from)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id));
    }
    
    public PageResponse<EmployeeDto.Response> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return PageResponse.of(employees.map(EmployeeDto.Response::from));
    }
    
    public List<EmployeeDto.Response> getEmployeesByDepartment(String departmentId) {
        Department department = findDepartment(departmentId);
        return employeeRepository.findByDepartment(department).stream()
            .map(EmployeeDto.Response::from)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateEmployee(String id, EmployeeDto.UpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id));
        
        if (request.getDepartmentId() != null) {
            Department department = findDepartment(request.getDepartmentId());
            employee.assignToDepartment(department);
        }
        
        if (request.getPositionId() != null) {
            Position position = findPosition(request.getPositionId());
            employee.promoteToPosition(position);
        }
        
        employee.updatePersonalInfo(request.getPhone(), request.getAddress());
    }
    
    @Transactional
    public void updatePassword(String id, String newPassword) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id));
        
        employee.updatePassword(passwordEncoder.encode(newPassword));
    }
    
    @Transactional
    public void deleteEmployee(String id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
    }
    
    private Department findDepartment(String departmentId) {
        return departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException("Department", departmentId));
    }
    
    private Position findPosition(String positionId) {
        return positionRepository.findById(positionId)
            .orElseThrow(() -> new EntityNotFoundException("Position", positionId));
    }
    
    private void validateUniqueEmail(String email) {
        if (employeeRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(
                String.format("Email already exists: %s", email),
                "DUPLICATE_EMAIL"
            );
        }
    }
}