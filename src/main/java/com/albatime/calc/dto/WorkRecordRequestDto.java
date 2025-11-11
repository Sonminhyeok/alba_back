package com.albatime.calc.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRecordRequestDto {

    @NotNull(message = "근무 날짜는 필수입니다.")
    private LocalDate workDate;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalTime startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalTime endTime;

    @NotNull(message = "시급은 필수입니다.")
    @Min(value = 1, message = "시급은 1원 이상이어야 합니다.")
    private Integer hourlyWage;

    @Size(max = 500, message = "메모는 500자 이하여야 합니다.")
    private String memo;
}