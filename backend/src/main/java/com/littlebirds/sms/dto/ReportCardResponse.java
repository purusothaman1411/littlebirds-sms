package com.littlebirds.sms.dto;

public record ReportCardResponse(StudentResponse student, StudentMarksResponse marks, AttendanceSummary attendance) {
}
