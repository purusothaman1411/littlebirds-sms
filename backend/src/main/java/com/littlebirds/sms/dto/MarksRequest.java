package com.littlebirds.sms.dto;

import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/** Subject name to mark. Only the subjects sent are added or updated. */
public record MarksRequest(

        @NotEmpty(message = "At least one subject mark is required")
        Map<@NotBlank(message = "Subject name cannot be blank") String,
            @NotNull(message = "Mark is required")
            @Min(value = 0, message = "Invalid Mark! Enter a mark between 0 and 100.")
            @Max(value = 100, message = "Invalid Mark! Enter a mark between 0 and 100.") Integer> marks) {
}
