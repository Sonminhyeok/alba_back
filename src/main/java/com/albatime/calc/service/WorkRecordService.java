package com.albatime.calc.service;

import java.time.LocalDate;
import java.util.List;

import com.albatime.calc.dto.MonthlySummaryDto;
import com.albatime.calc.dto.WorkRecordRequestDto;
import com.albatime.calc.dto.WorkRecordResponseDto;

public interface WorkRecordService {

    WorkRecordResponseDto createWorkRecord(WorkRecordRequestDto requestDto);

    WorkRecordResponseDto getWorkRecord(Long id);

    List<WorkRecordResponseDto> getAllWorkRecords();

    List<WorkRecordResponseDto> getWorkRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    List<WorkRecordResponseDto> getWorkRecordsByYearAndMonth(int year, int month);

    MonthlySummaryDto getMonthlySummary(int year, int month);

    WorkRecordResponseDto updateWorkRecord(Long id, WorkRecordRequestDto requestDto);

    void deleteWorkRecord(Long id);
}