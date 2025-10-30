package com.erp.entity;

import com.erp.entity.enums.Employment;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity implements UserDetails {
    
    @Id
    @Column(name = "employeeId")
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nameEng;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String rrn;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String address;
    private String addressDetails;
    private String phone;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate quitDate;
    private String internalNumber; 
    private String familyCertificate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId")
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionId")
    private Position position;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "employmentType", nullable = false)
    private EmploymentType employmentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    private Nationality nationality;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL) // 직원을 저장/ 삭제할 때 관련 학력/ 경력 정보도 삭제됨.
    private List<Education> educations = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences = new ArrayList<>();
    
    // Security related methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // Business methods
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
    
    public void updatePersonalInfo(String phone, String address) {
        this.phone = phone;
        this.address = address;
    }
    
    public void assignToDepartment(Department department) {
        this.department = department;
    }
    
    public void promoteToPosition(Position position) {
        this.position = position;
    }
}