package com.albatime.calc.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albatime.calc.dto.MonthlySummaryDto;
import com.albatime.calc.dto.WorkRecordRequestDto;
import com.albatime.calc.dto.WorkRecordResponseDto;
import com.albatime.calc.entity.WorkRecord;
import com.albatime.calc.repository.WorkRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkRecordServiceImpl implements WorkRecordService {

    private final WorkRecordRepository workRecordRepository;

    @Override
    @Transactional
    public WorkRecordResponseDto createWorkRecord(WorkRecordRequestDto requestDto) {
        try {
            validateWorkTime(requestDto.getStartTime().toString(), requestDto.getEndTime().toString());
            
            WorkRecord workRecord = WorkRecord.builder()
                    .workDate(requestDto.getWorkDate())
                    .startTime(requestDto.getStartTime())
                    .endTime(requestDto.getEndTime())
                    .hourlyWage(requestDto.getHourlyWage())
                    .memo(requestDto.getMemo())
                    .build();

            WorkRecord savedWorkRecord = workRecordRepository.save(workRecord);
            log.info("근무 기록 생성 완료 - ID: {}, 날짜: {}", savedWorkRecord.getId(), savedWorkRecord.getWorkDate());
            
            return WorkRecordResponseDto.from(savedWorkRecord);
        } catch (IllegalArgumentException e) {
            log.error("근무 기록 생성 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("근무 기록 생성 중 오류 발생", e);
            throw new RuntimeException("근무 기록 생성에 실패했습니다.", e);
        }
    }

    @Override
    public WorkRecordResponseDto getWorkRecord(Long id) {
        try {
            WorkRecord workRecord = findWorkRecordById(id);
            return WorkRecordResponseDto.from(workRecord);
        } catch (IllegalArgumentException e) {
            log.error("근무 기록 조회 실패 - ID: {}", id);
            throw e;
        }
    }

    @Override
    public List<WorkRecordResponseDto> getAllWorkRecords() {
        try {
            List<WorkRecord> workRecords = workRecordRepository.findAll();
            log.info("전체 근무 기록 조회 완료 - 총 {}건", workRecords.size());
            
            return workRecords.stream()
                    .map(WorkRecordResponseDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("전체 근무 기록 조회 중 오류 발생", e);
            throw new RuntimeException("근무 기록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<WorkRecordResponseDto> getWorkRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            validateDateRange(startDate, endDate);
            
            List<WorkRecord> workRecords = workRecordRepository.findByWorkDateBetweenOrderByWorkDateAsc(startDate, endDate);
            log.info("날짜 범위 조회 완료 - {}~{}, 총 {}건", startDate, endDate, workRecords.size());
            
            return workRecords.stream()
                    .map(WorkRecordResponseDto::from)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("날짜 범위 조회 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("날짜 범위 조회 중 오류 발생", e);
            throw new RuntimeException("근무 기록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<WorkRecordResponseDto> getWorkRecordsByYearAndMonth(int year, int month) {
        try {
            validateYearAndMonth(year, month);
            
            List<WorkRecord> workRecords = workRecordRepository.findByYearAndMonth(year, month);
            log.info("월별 근무 기록 조회 완료 - {}년 {}월, 총 {}건", year, month, workRecords.size());
            
            return workRecords.stream()
                    .map(WorkRecordResponseDto::from)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("월별 조회 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("월별 조회 중 오류 발생", e);
            throw new RuntimeException("근무 기록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public MonthlySummaryDto getMonthlySummary(int year, int month) {
        try {
            validateYearAndMonth(year, month);
            
            List<WorkRecord> workRecords = workRecordRepository.findByYearAndMonth(year, month);

            int workDays = workRecords.size();
            double totalWorkHours = workRecords.stream()
                    .mapToDouble(WorkRecord::calculateWorkHours)
                    .sum();
            int totalWage = workRecords.stream()
                    .mapToInt(WorkRecord::calculateTotalWage)
                    .sum();
            int averageHourlyWage = workRecords.isEmpty() ? 0 :
                    (int) workRecords.stream()
                            .mapToInt(WorkRecord::getHourlyWage)
                            .average()
                            .orElse(0);

            log.info("월별 통계 조회 완료 - {}년 {}월, 근무일수: {}일, 총급여: {}원", year, month, workDays, totalWage);

            return MonthlySummaryDto.builder()
                    .year(year)
                    .month(month)
                    .workDays(workDays)
                    .totalWorkHours(totalWorkHours)
                    .totalWage(totalWage)
                    .averageHourlyWage(averageHourlyWage)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("월별 통계 조회 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("월별 통계 조회 중 오류 발생", e);
            throw new RuntimeException("월별 통계 조회에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public WorkRecordResponseDto updateWorkRecord(Long id, WorkRecordRequestDto requestDto) {
        try {
            validateWorkTime(requestDto.getStartTime().toString(), requestDto.getEndTime().toString());
            
            WorkRecord workRecord = findWorkRecordById(id);

            workRecord.setWorkDate(requestDto.getWorkDate());
            workRecord.setStartTime(requestDto.getStartTime());
            workRecord.setEndTime(requestDto.getEndTime());
            workRecord.setHourlyWage(requestDto.getHourlyWage());
            workRecord.setMemo(requestDto.getMemo());

            log.info("근무 기록 수정 완료 - ID: {}", id);
            
            return WorkRecordResponseDto.from(workRecord);
        } catch (IllegalArgumentException e) {
            log.error("근무 기록 수정 실패 - ID: {}, 이유: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("근무 기록 수정 중 오류 발생", e);
            throw new RuntimeException("근무 기록 수정에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public void deleteWorkRecord(Long id) {
        try {
            if (!workRecordRepository.existsById(id)) {
                throw new IllegalArgumentException("근무 기록을 찾을 수 없습니다. ID: " + id);
            }
            
            workRecordRepository.deleteById(id);
            log.info("근무 기록 삭제 완료 - ID: {}", id);
        } catch (IllegalArgumentException e) {
            log.error("근무 기록 삭제 실패 - ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("근무 기록 삭제 중 오류 발생", e);
            throw new RuntimeException("근무 기록 삭제에 실패했습니다.", e);
        }
    }

    // === Private Helper Methods ===

    private WorkRecord findWorkRecordById(Long id) {
        return workRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다. ID: " + id));
    }

    private void validateWorkTime(String startTime, String endTime) {
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }
    }

    private void validateYearAndMonth(int year, int month) {
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("유효하지 않은 연도입니다: " + year);
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("유효하지 않은 월입니다: " + month);
        }
    }
}