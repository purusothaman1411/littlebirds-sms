package com.littlebirds.sms.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.littlebirds.sms.entity.AttendanceRecord;
import com.littlebirds.sms.entity.AttendanceStatus;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByStudent_StudentIdOrderByAttendanceDate(String studentId);

    Optional<AttendanceRecord> findByStudent_StudentIdAndAttendanceDate(String studentId, LocalDate date);

    List<AttendanceRecord> findByStudent_StandardAndAttendanceDate(Integer standard, LocalDate date);

    List<AttendanceRecord> findByAttendanceDate(LocalDate date);

    long countByStudent_StudentIdAndStatus(String studentId, AttendanceStatus status);
}
