package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "education")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Education extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "educationId")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    
    private String schoolName;
    private String major;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree")
    private Degree degree;

    @Enumerated(EnumType.STRING)
    @Column(name = "graduationStatus")
    private GraduationStatus graduationStatus;
    
    @Column(name = "admissionDate") // 입학일
    private LocalDate admissionDate;
    
    @Column(name = "graduationDate") // 졸업일
    private LocalDate graduationDate;
    
}