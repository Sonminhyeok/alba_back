package com.albatime.calc.repository;

import com.albatime.calc.entity.WorkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkRecordRepository extends JpaRepository<WorkRecord, Long> {

    // 특정 날짜의 근무 기록 조회
    List<WorkRecord> findByWorkDate(LocalDate workDate);

    // 날짜 범위로 근무 기록 조회
    List<WorkRecord> findByWorkDateBetweenOrderByWorkDateAsc(LocalDate startDate, LocalDate endDate);

    // 특정 년/월의 근무 기록 조회
    @Query("SELECT w FROM WorkRecord w WHERE YEAR(w.workDate) = :year AND MONTH(w.workDate) = :month ORDER BY w.workDate ASC")
    List<WorkRecord> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // 특정 년도의 근무 기록 조회
    @Query("SELECT w FROM WorkRecord w WHERE YEAR(w.workDate) = :year ORDER BY w.workDate ASC")
    List<WorkRecord> findByYear(@Param("year") int year);
}