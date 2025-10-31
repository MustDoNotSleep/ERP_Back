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
    public Long registerEmployee(EmployeeDto.Request request) {
        validateUniqueEmail(request.getEmail());
        
        Department department = findDepartment(request.getDepartmentId());
        Position position = findPosition(request.getPositionId());
        
        Employee employee = Employee.builder()
            .name(request.getName())
            .nameEng(request.getNameEng())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .rrn(request.getRrn())
            .phone(request.getPhone())
            .address(request.getAddress())
            .addressDetails(request.getAddressDetails())
            .birthDate(request.getBirthDate())
            .hireDate(request.getHireDate())
            .quitDate(request.getQuitDate())
            .internalNumber(request.getInternalNumber())
            .familyCertificate(request.getFamilyCertificate())
            .department(department)
            .position(position)
            .employmentType(request.getEmploymentType())
            .nationality(request.getNationality())
            .build();
        
        employeeRepository.save(employee);
        return employee.getId();
    }
    
    public EmployeeDto.Response getEmployee(Long id) {
        return employeeRepository.findById(id)
            .map(EmployeeDto.Response::from)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
    }
    
    public PageResponse<EmployeeDto.Response> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return PageResponse.of(employees.map(EmployeeDto.Response::from));
    }
    
    public PageResponse<EmployeeDto.Response> searchEmployees(String name, String email, 
                                                              Long departmentId, Long positionId, 
                                                              Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("id"), departmentId));
            }
            if (positionId != null) {
                predicates.add(criteriaBuilder.equal(root.get("position").get("id"), positionId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
        
        return PageResponse.of(employees.map(EmployeeDto.Response::from));
    }
    
    public List<EmployeeDto.Response> getEmployeesByDepartment(Long departmentId) {
        Department department = findDepartment(departmentId);
        return employeeRepository.findByDepartment(department).stream()
            .map(EmployeeDto.Response::from)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateEmployee(Long id, EmployeeDto.UpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
        
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
    public void updatePassword(Long id, String newPassword) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
        
        employee.updatePassword(passwordEncoder.encode(newPassword));
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee", id.toString());
        }
        employeeRepository.deleteById(id);
    }
    
    private Department findDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
            .orElseThrow(() -> new EntityNotFoundException("Department", departmentId.toString()));
    }
    
    private Position findPosition(Long positionId) {
        return positionRepository.findById(positionId)
            .orElseThrow(() -> new EntityNotFoundException("Position", positionId.toString()));
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