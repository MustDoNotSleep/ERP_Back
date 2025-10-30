package com.erp.repository;

import com.erp.entity.Employee;
import com.erp.entity.MilitaryServiceInfo;
import com.erp.entity.enums.MilitaryStatus;
import com.erp.entity.enums.MilitaryBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilitaryServiceInfoRepository extends JpaRepository<MilitaryServiceInfo, Long> {
    Optional<MilitaryServiceInfo> findByEmployee(Employee employee);
    List<MilitaryServiceInfo> findByMilitaryStatus(MilitaryStatus militaryStatus);
    List<MilitaryServiceInfo> findByMilitaryBranch(MilitaryBranch militaryBranch);
}