package com.erp.entity;

import com.erp.entity.enums.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendanceId")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    
    @Column(name = "checkIn", nullable = false)
    private LocalDateTime checkIn;
    
    @Column(name = "checkOut")
    private LocalDateTime checkOut;
    
    @Column(nullable = false)
    private AttendanceType attendanceType;
    
    private String note;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leaveId")
    private Leave leave; // 휴가 참조 (휴가인 경우에만)
    
    @Column(name = "workHours")
    private Double workHours;
    
    @Column(name = "overtimeHours")
    private Double overtimeHours; // 초과근무 시간
    
    // Business methods
    public void setLeave(Leave leave) {
        this.leave = leave;
    }
    
    public boolean isOnLeave() {
        return this.leave != null;
    }
    
    public void checkOut(LocalDateTime checkOutTime) {
        this.checkOut = checkOutTime;
        calculateWorkHours();
        calculateOvertimeHours();
    }
    
    public void updateCheckOut(LocalDateTime checkOutTime) {
        this.checkOut = checkOutTime;
    }
    
    public void updateWorkHours(Double workHours) {
        this.workHours = workHours;
    }
    
    public void updateOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public void updateAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }
    
    public void updateNote(String note) {
        if (note != null) {
            this.note = note;
        }
    }
    
    private void calculateWorkHours() {
        if (checkIn != null && checkOut != null) {
            long hours = java.time.Duration.between(checkIn, checkOut).toHours();
            long minutes = java.time.Duration.between(checkIn, checkOut).toMinutes() % 60;
            this.workHours = hours + minutes / 60.0;
        }
    }
    
    private void calculateOvertimeHours() {
        if (workHours != null && workHours > 8.0) {
            // 기본 근무시간(8시간) 초과분을 초과근무로 계산
            this.overtimeHours = workHours - 8.0;
        } else {
            this.overtimeHours = 0.0;
        }
    }
}