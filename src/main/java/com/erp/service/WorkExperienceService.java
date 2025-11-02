package com.erp.service;

import com.erp.dto.WorkExperienceDto;
import com.erp.entity.WorkExperience;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.WorkExperienceRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final EmployeeRepository employeeRepository;

    public List<WorkExperienceDto.Response> getAllWorkExperiences() {
        return workExperienceRepository.findAll().stream()
                .map(WorkExperienceDto.Response::from)
                .collect(Collectors.toList());
    }

    public List<WorkExperienceDto.Response> getWorkExperiencesByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return workExperienceRepository.findByEmployee(employee).stream()
                .map(WorkExperienceDto.Response::from)
                .collect(Collectors.toList());
    }

    public WorkExperienceDto.Response getWorkExperienceById(Long id) {
        WorkExperience workExperience = workExperienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkExperience", id.toString()));
        return WorkExperienceDto.Response.from(workExperience);
    }

    @Transactional
    public WorkExperienceDto.Response createWorkExperience(Long employeeId, WorkExperienceDto.Request request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));

        WorkExperience workExperience = WorkExperience.builder()
                .employee(employee)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .finalPosition(request.getFinalPosition())
                .finalSalary(request.getFinalSalary())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        WorkExperience saved = workExperienceRepository.save(workExperience);
        return WorkExperienceDto.Response.from(saved);
    }

    @Transactional
    public WorkExperienceDto.Response updateWorkExperience(Long id, WorkExperienceDto.UpdateRequest request) {
        WorkExperience workExperience = workExperienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkExperience", id.toString()));

        workExperienceRepository.delete(workExperience);
        
        WorkExperience updated = WorkExperience.builder()
                .id(id)
                .employee(workExperience.getEmployee())
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .finalPosition(request.getFinalPosition())
                .finalSalary(request.getFinalSalary())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        WorkExperience saved = workExperienceRepository.save(updated);
        return WorkExperienceDto.Response.from(saved);
    }

    @Transactional
    public void deleteWorkExperience(Long id) {
        if (!workExperienceRepository.existsById(id)) {
            throw new EntityNotFoundException("WorkExperience", id.toString());
        }
        workExperienceRepository.deleteById(id);
    }
}
