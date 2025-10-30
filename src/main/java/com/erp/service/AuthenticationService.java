package com.erp.service;

import com.erp.entity.Employee;
import com.erp.dto.auth.AuthenticationRequest;
import com.erp.dto.auth.AuthenticationResponse;
import com.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {
    
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. 인증 수행
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // 2. 사용자 정보 조회
        Employee employee = employeeRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 3. JWT 토큰 생성
        String accessToken = jwtService.generateToken(employee);
        String refreshToken = jwtService.generateRefreshToken(employee);
        
        // 4. 응답 생성
        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L) // 24시간
            .employeeId(employee.getId())
            .name(employee.getName())
            .email(employee.getEmail())
            .role(getRoleName(employee))
            .department(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
            .position(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
            .build();
    }
    
    private String getRoleName(Employee employee) {
        return employee.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .findFirst()
            .orElse("ROLE_USER");
    }
}
