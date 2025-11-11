package com.albatime.calc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummaryDto {

    private Integer year;  // 년도
    private Integer month;  // 월
    private Integer workDays;  // 근무 일수
    private Double totalWorkHours;  // 총 근무 시간
    private Integer totalWage;  // 총 급여
    private Integer averageHourlyWage;  // 평균 시급
}