package com.albatime.calc.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate workDate;  // 근무 날짜

    @Column(nullable = false)
    private LocalTime startTime;  // 시작 시간

    @Column(nullable = false)
    private LocalTime endTime;  // 종료 시간

    @Column(nullable = false)
    private Integer hourlyWage;  // 시급 (원)

    @Column(length = 500)
    private String memo;  // 메모

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 수정 시간

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 근무 시간 계산 (분 단위)
    public long calculateWorkMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // 근무 시간 계산 (시간 단위, 소수점)
    public double calculateWorkHours() {
        return calculateWorkMinutes() / 60.0;
    }

    // 총 급여 계산
    public int calculateTotalWage() {
        double hours = calculateWorkHours();
        return (int) (hours * hourlyWage);
    }
}