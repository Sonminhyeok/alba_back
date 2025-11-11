package com.albatime.calc.dto;

import com.albatime.calc.entity.WorkRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRecordResponseDto {

    private Long id;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer hourlyWage;
    private String memo;
    private Double workHours;  // 근무 시간
    private Integer totalWage;  // 총 급여
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public static WorkRecordResponseDto from(WorkRecord workRecord) {
        return WorkRecordResponseDto.builder()
                .id(workRecord.getId())
                .workDate(workRecord.getWorkDate())
                .startTime(workRecord.getStartTime())
                .endTime(workRecord.getEndTime())
                .hourlyWage(workRecord.getHourlyWage())
                .memo(workRecord.getMemo())
                .workHours(workRecord.calculateWorkHours())
                .totalWage(workRecord.calculateTotalWage())
                .createdAt(workRecord.getCreatedAt())
                .updatedAt(workRecord.getUpdatedAt())
                .build();
    }
}