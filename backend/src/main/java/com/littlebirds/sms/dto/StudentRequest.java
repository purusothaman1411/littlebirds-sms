package com.littlebirds.sms.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used for create and update. On update the ID comes from the URL and studentId here is ignored.
 * Rules come from the console app's InputHelper. The date of birth is checked "not in the future"
 * in the service, using the school's time zone.
 */
public record StudentRequest(

        @NotBlank(groups = OnCreate.class, message = "Student ID is required")
        @Pattern(groups = OnCreate.class, regexp = "[A-Za-z][A-Za-z0-9_-]{0,19}",
                message = "Invalid Student ID. Start with a letter; use letters, numbers, _ or - (max 20), e.g. S101")
        String studentId,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name can be at most 100 characters")
        @Pattern(regexp = "[a-zA-Z ]*", message = "Name should contain letters and spaces only")
        String name,

        @NotNull(message = "Date of birth is required (yyyy-MM-dd)")
        LocalDate dob,

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "(?i)male|female|other", message = "Gender must be Male, Female or Other")
        String gender,

        @NotNull(message = "Standard is required")
        @Min(value = 1, message = "Standard must be between 1 and 12")
        @Max(value = 12, message = "Standard must be between 1 and 12")
        Integer standard,

        String group,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address can be at most 255 characters")
        String address,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "[6-9][0-9]{9}", message = "Contact number must be exactly 10 digits and start with 6-9")
        String contactNumber) {
}
