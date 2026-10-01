package com.littlebirds.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** status is "P" or "A" (case-insensitive). */
public record AttendanceEntry(

        @NotBlank(message = "Student ID is required for every attendance entry")
        String studentId,

        @NotBlank(message = "Attendance status is required (P or A)")
        @Pattern(regexp = "(?i)P|A", message = "Invalid Attendance! Enter P for Present or A for Absent.")
        String status) {
}
