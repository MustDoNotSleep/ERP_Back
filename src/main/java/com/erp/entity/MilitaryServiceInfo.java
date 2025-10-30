package com.erp.entity;

import com.erp.entity.enums.MilitaryStatus;
import com.erp.entity.enums.MilitaryBranch;
import com.erp.entity.enums.MilitaryRank;
import com.erp.entity.enums.MilitarySpecialty;
import com.erp.entity.enums.ExemptionReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "MilitaryServiceInfo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MilitaryServiceInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "militaryInfoId")
    private Long id;
    
    // 2. employeeId (Foreign Key, Employee와의 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee; 

    // 3. militaryStatus (현역, 미복, 현역;면제받았음)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MilitaryStatus militaryStatus;

    // 4. militaryBranch (육군, 해군, 공군, 해병대)
    @Enumerated(EnumType.STRING)
    private MilitaryBranch militaryBranch;

    // 5. militaryRank (병장, 상병, 일병, 하사)
    @Enumerated(EnumType.STRING)
    private MilitaryRank militaryRank;

    // 6. militarySpecialty (보병, 포병, 통신, 공병)
    @Enumerated(EnumType.STRING)
    private MilitarySpecialty militarySpecialty;

    // 7. exemptionReason (복무대기, 생계곤란, 질병)
    @Enumerated(EnumType.STRING)
    private ExemptionReason exemptionReason;

    // 8. serviceStartDate
    @Column(name = "serviceStartDate")
    private LocalDate serviceStartDate;

    // 9. serviceEndDate
    @Column(name = "serviceEndDate")
    private LocalDate serviceEndDate;

    /*
     * 비즈니스 로직 추가 영역
     */
}