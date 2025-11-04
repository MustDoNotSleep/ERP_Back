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
import com.erp.scheduler.AnnualLeaveScheduler;
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
    private final AnnualLeaveScheduler annualLeaveScheduler;
    
    @Transactional
    public Long registerEmployee(EmployeeDto.Request request) {
        validateUniqueEmail(request.getEmail());
        
        // Department 자동 생성
        Department department = Department.builder()
            .departmentName(request.getDepartmentName())
            .teamName(request.getTeamName())
            .isManagement(false)
            .build();
        departmentRepository.save(department);
        
        // Position 자동 생성 (positionLevel은 positionName에 따라 자동 매핑)
        Position position = Position.builder()
            .positionName(request.getPositionName())
            .positionLevel(getPositionLevel(request.getPositionName()))
            .build();
        positionRepository.save(position);
        
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
        
        // 신규 직원 연차 자동 배급
        try {
            annualLeaveScheduler.generateLeaveForNewEmployee(employee);
        } catch (Exception e) {
            // 연차 배급 실패해도 직원 등록은 완료
            // 다음 스케줄러 실행 시 자동 처리됨
        }
        
        return employee.getId();
    }
    
    private Integer getPositionLevel(String positionName) {
        return switch (positionName) {
            case "사원" -> 1;
            case "주임" -> 2;
            case "대리" -> 3;
            case "과장" -> 4;
            case "차장" -> 5;
            case "부장" -> 6;
            case "이사" -> 7;
            case "상무" -> 8;
            case "전무" -> 9;
            case "부사장" -> 10;
            case "사장" -> 11;
            default -> 1; // 기본값: 사원급
        };
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
    
    public PageResponse<EmployeeDto.Response> searchEmployees(String name, String email, String employeeId,
                                                              String departmentName, String positionName,
                                                              Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            if (employeeId != null && !employeeId.trim().isEmpty()) {
                try {
                    // 숫자로 변환 가능하면 정확히 일치하는 것 검색
                    Long id = Long.parseLong(employeeId);
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                } catch (NumberFormatException e) {
                    // 숫자가 아니면 문자열로 LIKE 검색 (시작 부분 일치만)
                    predicates.add(criteriaBuilder.like(root.get("id").as(String.class), employeeId + "%"));
                }
            }
            if (departmentName != null && !departmentName.trim().isEmpty()) {
                // LIKE 검색으로 변경 (부분 일치)
                predicates.add(criteriaBuilder.like(root.get("department").get("departmentName"), "%" + departmentName + "%"));
            }
            if (positionName != null && !positionName.trim().isEmpty()) {
                // LIKE 검색으로 변경 (부분 일치)
                predicates.add(criteriaBuilder.like(root.get("position").get("positionName"), "%" + positionName + "%"));
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