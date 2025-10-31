package com.erp.service;

import com.erp.dto.MilitaryServiceInfoDto;
import com.erp.entity.MilitaryServiceInfo;
import com.erp.entity.Employee;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.MilitaryServiceInfoRepository;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilitaryServiceInfoService {

    private final MilitaryServiceInfoRepository militaryServiceInfoRepository;
    private final EmployeeRepository employeeRepository;

    public Optional<MilitaryServiceInfoDto.Response> getMilitaryServiceInfoByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));
        
        return militaryServiceInfoRepository.findByEmployee(employee)
                .map(MilitaryServiceInfoDto.Response::from);
    }

    public MilitaryServiceInfoDto.Response getMilitaryServiceInfoById(Long id) {
        MilitaryServiceInfo militaryServiceInfo = militaryServiceInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MilitaryServiceInfo", id.toString()));
        return MilitaryServiceInfoDto.Response.from(militaryServiceInfo);
    }

    @Transactional
    public MilitaryServiceInfoDto.Response createMilitaryServiceInfo(Long employeeId, MilitaryServiceInfoDto.Request request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId.toString()));

        if (militaryServiceInfoRepository.findByEmployee(employee).isPresent()) {
            throw new IllegalStateException("Military service information already exists for employee: " + employeeId);
        }

        MilitaryServiceInfo militaryServiceInfo = MilitaryServiceInfo.builder()
                .employee(employee)
                .militaryStatus(request.getMilitaryStatus())
                .militaryBranch(request.getMilitaryBranch())
                .militaryRank(request.getMilitaryRank())
                .militarySpecialty(request.getMilitarySpecialty())
                .exemptionReason(request.getExemptionReason())
                .serviceStartDate(request.getServiceStartDate())
                .serviceEndDate(request.getServiceEndDate())
                .build();

        MilitaryServiceInfo saved = militaryServiceInfoRepository.save(militaryServiceInfo);
        return MilitaryServiceInfoDto.Response.from(saved);
    }

    @Transactional
    public MilitaryServiceInfoDto.Response updateMilitaryServiceInfo(Long id, MilitaryServiceInfoDto.UpdateRequest request) {
        MilitaryServiceInfo militaryServiceInfo = militaryServiceInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MilitaryServiceInfo", id.toString()));

        militaryServiceInfoRepository.delete(militaryServiceInfo);
        
        MilitaryServiceInfo updated = MilitaryServiceInfo.builder()
                .id(id)
                .employee(militaryServiceInfo.getEmployee())
                .militaryStatus(request.getMilitaryStatus())
                .militaryBranch(request.getMilitaryBranch())
                .militaryRank(request.getMilitaryRank())
                .militarySpecialty(request.getMilitarySpecialty())
                .exemptionReason(request.getExemptionReason())
                .serviceStartDate(request.getServiceStartDate())
                .serviceEndDate(request.getServiceEndDate())
                .build();

        MilitaryServiceInfo saved = militaryServiceInfoRepository.save(updated);
        return MilitaryServiceInfoDto.Response.from(saved);
    }

    @Transactional
    public void deleteMilitaryServiceInfo(Long id) {
        if (!militaryServiceInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("MilitaryServiceInfo", id.toString());
        }
        militaryServiceInfoRepository.deleteById(id);
    }
}
