package com.littlebirds.sms.service;

import java.time.LocalDate;
import java.time.ZoneId;

import com.littlebirds.sms.entity.Gender;
import com.littlebirds.sms.exception.InvalidRequestException;

/** Small business-rule helpers shared by several services. Format checks live in Bean Validation (Phase 8). */
final class ValidationRules {

    static final ZoneId SCHOOL_ZONE = ZoneId.of("Asia/Kolkata");

    private ValidationRules() {
    }

    static LocalDate today() {
        return LocalDate.now(SCHOOL_ZONE);
    }

    static Gender parseGender(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("Gender is required");
        }
        try {
            return Gender.fromLabel(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    /** Console rule: a date of birth cannot be in the future. */
    static LocalDate requireNotFuture(LocalDate date, String fieldName) {
        if (date == null) {
            throw new InvalidRequestException(fieldName + " is required");
        }
        if (date.isAfter(today())) {
            throw new InvalidRequestException(fieldName + " cannot be in the future");
        }
        return date;
    }
}
