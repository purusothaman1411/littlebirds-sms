package com.littlebirds.sms.dto;

import java.time.LocalDate;

import com.littlebirds.sms.entity.AttendanceRecord;

public record AttendanceRecordDto(String studentId, String studentName, LocalDate date, String status) {

    public static AttendanceRecordDto from(AttendanceRecord r) {
        return new AttendanceRecordDto(r.getStudent().getStudentId(), r.getStudent().getName(),
                r.getAttendanceDate(), r.getStatus().getCode());
    }
}
