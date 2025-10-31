package com.erp.service;

import com.erp.dto.SalaryInfoDto;
import com.erp.entity.SalaryInfo;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.SalaryInfoRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryInfoService {

    private final SalaryInfoRepository salaryInfoRepository;
    private final EmployeeRepository employeeRepository;

    public Optional<SalaryInfoDto.Response> getSalaryInfoByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return salaryInfoRepository.findByEmployee(employee)
                .map(SalaryInfoDto.Response::from);
    }

    public SalaryInfoDto.Response getSalaryInfoById(Long id) {
        SalaryInfo salaryInfo = salaryInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SalaryInfo", id.toString()));
        return SalaryInfoDto.Response.from(salaryInfo);
    }

    @Transactional
    public SalaryInfoDto.Response createSalaryInfo(Long employeeId, SalaryInfoDto.Request request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));

        if (salaryInfoRepository.findByEmployee(employee).isPresent()) {
            throw new IllegalStateException("Salary information already exists for employee: " + employeeId);
        }

        SalaryInfo salaryInfo = SalaryInfo.builder()
                .employee(employee)
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .build();

        SalaryInfo saved = salaryInfoRepository.save(salaryInfo);
        return SalaryInfoDto.Response.from(saved);
    }

    @Transactional
    public SalaryInfoDto.Response updateSalaryInfo(Long id, SalaryInfoDto.UpdateRequest request) {
        SalaryInfo salaryInfo = salaryInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SalaryInfo", id.toString()));

        salaryInfoRepository.delete(salaryInfo);
        
        SalaryInfo updated = SalaryInfo.builder()
                .id(id)
                .employee(salaryInfo.getEmployee())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .build();

        SalaryInfo saved = salaryInfoRepository.save(updated);
        return SalaryInfoDto.Response.from(saved);
    }

    @Transactional
    public void deleteSalaryInfo(Long id) {
        if (!salaryInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("SalaryInfo", id.toString());
        }
        salaryInfoRepository.deleteById(id);
    }
}
