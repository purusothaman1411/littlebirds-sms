package com.littlebirds.sms.dto;

import java.util.List;

public record StudentAttendanceResponse(
        String studentId,
        String studentName,
        AttendanceSummary summary,
        List<AttendanceRecordDto> records) {
}
