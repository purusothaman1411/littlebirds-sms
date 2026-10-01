package com.littlebirds.sms.dto;

import java.time.LocalDate;

public record AttendanceSaveResponse(LocalDate date, Integer standard, int created, int updated, int present, int absent) {
}
