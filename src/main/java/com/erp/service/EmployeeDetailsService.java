package com.erp.service;

import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmployeeDetailsService implements UserDetailsService {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isEmpty()) {
            log.debug("User not found by email={}", email);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        var emp = employeeOpt.get();
        // Debug: log whether stored password looks like bcrypt (starts with $2)
        try {
            String pw = emp.getPassword();
            log.debug("Loaded user email={} passwordPresent={} bcryptLike={}", email, pw != null, pw != null && pw.startsWith("$2"));
        } catch (Exception e) {
            log.debug("Could not inspect password for user={}: {}", email, e.getMessage());
        }

        return emp;
    }
}
