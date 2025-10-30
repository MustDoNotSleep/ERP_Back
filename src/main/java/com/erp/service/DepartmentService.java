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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public String createDepartment(DepartmentDto.Request request) {
        validateUniqueName(request.getName());
        
        Department parentDepartment = null;
        if (request.getParentDepartmentId() != null) {
            parentDepartment = findDepartment(request.getParentDepartmentId());
        }
        
        Department department = Department.builder()
            .id(UUID.randomUUID().toString())
            .name(request.getName())
            .description(request.getDescription())
            .parentDepartment(parentDepartment)
            .build();
        
        departmentRepository.save(department);
        return department.getId();
    }
    
    public DepartmentDto.Response getDepartment(String id) {
        return departmentRepository.findById(id)
            .map(DepartmentDto.Response::from)
            .orElseThrow(() -> new EntityNotFoundException("Department", id));
    }
    
    public List<DepartmentDto.Response> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(DepartmentDto.Response::from)
            .collect(Collectors.toList());
    }
    
    public List<DepartmentDto.Response> getRootDepartments() {
        return departmentRepository.findByParentDepartmentIsNull().stream()
            .map(DepartmentDto.Response::from)
            .collect(Collectors.toList());
    }
    
    public List<DepartmentDto.Response> getChildDepartments(String parentId) {
        Department parent = findDepartment(parentId);
        return departmentRepository.findByParentDepartment(parent).stream()
            .map(DepartmentDto.Response::from)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateDepartment(String id, DepartmentDto.UpdateRequest request) {
        Department department = findDepartment(id);
        
        if (!department.getName().equals(request.getName())) {
            validateUniqueName(request.getName());
        }
        
        Department updatedDepartment = Department.builder()
            .id(department.getId())
            .name(request.getName())
            .description(request.getDescription())
            .parentDepartment(department.getParentDepartment())
            .build();
        
        departmentRepository.save(updatedDepartment);
    }
    
    @Transactional
    public void deleteDepartment(String id) {
        Department department = findDepartment(id);
        
        if (!department.getEmployees().isEmpty()) {
            throw new BusinessException(
                "Cannot delete department with employees",
                "DEPARTMENT_HAS_EMPLOYEES"
            );
        }
        
        if (!department.getChildDepartments().isEmpty()) {
            throw new BusinessException(
                "Cannot delete department with child departments",
                "DEPARTMENT_HAS_CHILDREN"
            );
        }
        
        departmentRepository.delete(department);
    }
    
    private Department findDepartment(String id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department", id));
    }
    
    private void validateUniqueName(String name) {
        if (departmentRepository.findByName(name).isPresent()) {
            throw new BusinessException(
                String.format("Department name already exists: %s", name),
                "DUPLICATE_DEPARTMENT_NAME"
            );
        }
    }
}