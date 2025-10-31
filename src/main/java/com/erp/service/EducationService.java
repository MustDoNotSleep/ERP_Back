package com.erp.service;

import com.erp.dto.EducationDto;
import com.erp.entity.Education;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.EducationRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final EmployeeRepository employeeRepository;

    public List<EducationDto.Response> getEducationsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return educationRepository.findByEmployee(employee).stream()
                .map(EducationDto.Response::from)
                .collect(Collectors.toList());
    }

    public EducationDto.Response getEducationById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Education", id.toString()));
        return EducationDto.Response.from(education);
    }

    @Transactional
    public EducationDto.Response createEducation(Long employeeId, EducationDto.Request request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));

        Education education = Education.builder()
                .employee(employee)
                .schoolName(request.getSchoolName())
                .major(request.getMajor())
                .degree(request.getDegree())
                .graduationStatus(request.getGraduationStatus())
                .admissionDate(request.getAdmissionDate())
                .graduationDate(request.getGraduationDate())
                .build();

        Education saved = educationRepository.save(education);
        return EducationDto.Response.from(saved);
    }

    @Transactional
    public EducationDto.Response updateEducation(Long id, EducationDto.UpdateRequest request) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Education", id.toString()));

        educationRepository.delete(education);
        
        Education updated = Education.builder()
                .id(id)
                .employee(education.getEmployee())
                .schoolName(request.getSchoolName())
                .major(request.getMajor())
                .degree(request.getDegree())
                .graduationStatus(request.getGraduationStatus())
                .admissionDate(request.getAdmissionDate())
                .graduationDate(request.getGraduationDate())
                .build();

        Education saved = educationRepository.save(updated);
        return EducationDto.Response.from(saved);
    }

    @Transactional
    public void deleteEducation(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Education", id.toString());
        }
        educationRepository.deleteById(id);
    }
}
