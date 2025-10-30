package com.erp.entity;

import com.erp.entity.converter.EmploymentTypeConverter;
import com.erp.entity.converter.NationalityConverter;
import com.erp.entity.enums.EmploymentType;
import com.erp.entity.enums.Nationality;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    @Convert(converter = EmploymentTypeConverter.class)
    @Column(name = "employmentType", nullable = false)
    private EmploymentType employmentType;
    
    @Convert(converter = NationalityConverter.class)
    @Column(name = "nationality", nullable = false)
    private Nationality nationality;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Education> educations = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();
    
    // Security related methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Position level 기반으로 권한 부여
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (position != null && position.getPositionLevel() != null) {
            int level = position.getPositionLevel();
            
            // Level에 따른 권한 부여
            if (level >= 5) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_HR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
            } else if (level >= 3) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
            }
            
            // 모든 직원은 기본 USER 권한
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            // Position이 없는 경우 기본 USER 권한만
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
    
    @Override
    public String getUsername() {
        return this.email;
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
    
    public void updateRrn(String newRrn) {
        this.rrn = newRrn;
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