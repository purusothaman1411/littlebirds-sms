package com.littlebirds.sms.dto;

public record AttendanceSummary(int presentDays, int absentDays, int totalDays, double percentage) {
}
