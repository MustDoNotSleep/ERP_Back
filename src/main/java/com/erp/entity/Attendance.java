// package com.erp.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.LocalDateTime;

// @Entity
// @Table(name = "attendance")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Builder
// public class Attendance extends BaseEntity {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "attendanceId")
//     private String id;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employeeId")
//     private Employee employee;
    
//     @Column(name = "checkIn", nullable = false)
//     private LocalDateTime checkIn;
    
//     @Column(name = "checkOut")
//     private LocalDateTime checkOut;
    
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private AttendanceType type;
    
//     private String note;
    
//     @Column(name = "workHours")
//     private Double workHours;
    
//     public enum AttendanceType {
//         NORMAL, LATE, EARLY_LEAVE, ABSENT, REMOTE
//     }
    
//     // Business methods
//     public void checkOut(LocalDateTime checkOutTime) {
//         this.checkOut = checkOutTime;
//         calculateWorkHours();
//     }
    
//     private void calculateWorkHours() {
//         if (checkIn != null && checkOut != null) {
//             this.workHours = checkOut.getHour() - checkIn.getHour() 
//                 + (checkOut.getMinute() - checkIn.getMinute()) / 60.0;
//         }
//     }
// }