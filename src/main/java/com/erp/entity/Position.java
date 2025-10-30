package com.erp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Position extends BaseEntity {
    
    @Id
    @Column(name = "positionId")
    private Long id;
    
    @Column(nullable = false)
    private String positionName;
    
    
    @Column(name = "positionLevel")
    private Integer positionLevel;
    
    @OneToMany(mappedBy = "position")
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}