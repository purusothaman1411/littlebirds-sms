package com.littlebirds.sms.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Attendance for a whole class on one date. Every student of the standard must be included.
 * "Not in the future" is checked in the service, using the school's time zone.
 */
public record AttendanceRequest(

        @NotNull(message = "Attendance date is required (yyyy-MM-dd)")
        LocalDate date,

        @NotNull(message = "Standard is required")
        @Min(value = 1, message = "Standard must be between 1 and 12")
        @Max(value = 12, message = "Standard must be between 1 and 12")
        Integer standard,

        @NotEmpty(message = "Attendance entries are required")
        List<@Valid AttendanceEntry> entries) {
}
