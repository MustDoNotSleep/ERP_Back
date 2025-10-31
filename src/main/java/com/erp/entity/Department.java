package com.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {
    
    @Id
    @Column(name = "departmentId")
    private Long id;
    
    @Column(nullable = false)
    private String departmentName;

    private String teamName;

    @Column(name = "manage")
    private boolean isManagement;
    
    @OneToMany(mappedBy = "department")
    @Builder.Default
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();
}