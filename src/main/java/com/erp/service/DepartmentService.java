package com.erp.service;

import com.erp.dto.DepartmentDto;
import com.erp.entity.Department;
import com.erp.exception.BusinessException;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public Long createDepartment(DepartmentDto.Request request) {
        validateUniqueName(request.getDepartmentName());
        
        Department department = Department.builder()
            .departmentName(request.getDepartmentName())
            .teamName(request.getTeamName())
            .isManagement(request.isManagement())
            .build();
        
        departmentRepository.save(department);
        return department.getId();
    }
    
    public DepartmentDto.Response getDepartment(Long id) {
        return departmentRepository.findById(id)
            .map(DepartmentDto.Response::from)
            .orElseThrow(() -> new EntityNotFoundException("Department", id.toString()));
    }
    
    public List<DepartmentDto.Response> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(DepartmentDto.Response::from)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateDepartment(Long id, DepartmentDto.UpdateRequest request) {
        Department department = findDepartment(id);
        
        if (!department.getDepartmentName().equals(request.getDepartmentName())) {
            validateUniqueName(request.getDepartmentName());
        }
        
        Department updatedDepartment = Department.builder()
            .id(department.getId())
            .departmentName(request.getDepartmentName())
            .teamName(request.getTeamName())
            .isManagement(request.isManagement())
            .build();
        
        departmentRepository.save(updatedDepartment);
    }
    
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = findDepartment(id);
        
        if (!department.getEmployees().isEmpty()) {
            throw new BusinessException(
                "Cannot delete department with employees",
                "DEPARTMENT_HAS_EMPLOYEES"
            );
        }
        
        departmentRepository.delete(department);
    }
    
    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department", id.toString()));
    }
    
    private void validateUniqueName(String name) {
        if (departmentRepository.findByDepartmentName(name).isPresent()) {
            throw new BusinessException(
                String.format("Department name already exists: %s", name),
                "DUPLICATE_DEPARTMENT_NAME"
            );
        }
    }
}