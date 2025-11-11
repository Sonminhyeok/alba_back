package com.albatime.calc.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.albatime.calc.dto.MonthlySummaryDto;
import com.albatime.calc.dto.WorkRecordRequestDto;
import com.albatime.calc.dto.WorkRecordResponseDto;
import com.albatime.calc.service.WorkRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/work-records")
@RequiredArgsConstructor
public class WorkRecordController {

    private final WorkRecordService workRecordService;

    @PostMapping
    public ResponseEntity<WorkRecordResponseDto> createWorkRecord(@RequestBody WorkRecordRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workRecordService.createWorkRecord(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkRecordResponseDto> getWorkRecord(@PathVariable("id") Long id) {
        return ResponseEntity.ok(workRecordService.getWorkRecord(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkRecordResponseDto>> getAllWorkRecords() {
        return ResponseEntity.ok(workRecordService.getAllWorkRecords());
    }

    @GetMapping("/range")
    public ResponseEntity<List<WorkRecordResponseDto>> getWorkRecordsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(workRecordService.getWorkRecordsByDateRange(startDate, endDate));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<WorkRecordResponseDto>> getWorkRecordsByYearAndMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return ResponseEntity.ok(workRecordService.getWorkRecordsByYearAndMonth(year, month));
    }

    @GetMapping("/monthly/summary")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        return ResponseEntity.ok(workRecordService.getMonthlySummary(year, month));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkRecordResponseDto> updateWorkRecord(
            @PathVariable("id") Long id,
            @RequestBody WorkRecordRequestDto requestDto) {
        return ResponseEntity.ok(workRecordService.updateWorkRecord(id, requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkRecord(@PathVariable("id") Long id) {
        workRecordService.deleteWorkRecord(id);
        return ResponseEntity.noContent().build();
    }
}