package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.MilitaryServiceInfo;
import com.erp.entity.enums.Military;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilitaryServiceInfoRepository extends JpaRepository<MilitaryServiceInfo, String> {
    Optional<MilitaryServiceInfo> findByEmployee(Employee employee);
    List<MilitaryServiceInfo> findByServiceType(Military.ServiceType serviceType);
    List<MilitaryServiceInfo> findByDischargeType(Military.DischargeType dischargeType);
}