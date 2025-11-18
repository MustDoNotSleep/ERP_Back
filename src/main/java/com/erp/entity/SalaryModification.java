package com.erp.entity;

import com.erp.entity.enums.UpdateTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "salary_modifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SalaryModification extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modificationId")
    private Long id;
    
    @Column(nullable = false)
    private YearMonth paymentDate; // 지급 월
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateTargetType targetType; // 수정 대상 유형 (ALL, DEPARTMENT, POSITION, EMPLOYEE)
    
    @Column(length = 100)
    private String targetName; // 대상 이름 (전체 직원, 개발팀, 대리, 홍길동)
    
    @Column(nullable = false)
    private Integer employeeCount; // 영향받은 직원 수

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // 1명당 추가된 금액
    
    @Column(length = 500)
    private String description; // 수정 사유 (bonusReason과 동일)
    
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정 일시
    
    @Column(length = 100)
    private String modifiedBy; // 수정한 사람
}
