package com.littlebirds.sms.dto;

import java.time.Instant;
import java.util.Map;

/**
 * The one error shape returned by the API. "errors" (field to message) is only present for
 * validation failures; null fields are left out of the JSON.
 */
public record ApiError(int status, String message, Instant timestamp, Map<String, String> errors) {

    public static ApiError of(int status, String message) {
        return new ApiError(status, message, Instant.now(), null);
    }

    public static ApiError of(int status, String message, Map<String, String> errors) {
        return new ApiError(status, message, Instant.now(), errors);
    }
}
