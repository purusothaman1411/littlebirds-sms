package com.littlebirds.sms.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used for create and update. On update the ID comes from the URL and teacherId here is ignored.
 * Rules come from the console app's InputHelper; there is no age field.
 */
public record TeacherRequest(

        @NotBlank(groups = OnCreate.class, message = "Teacher ID is required")
        @Pattern(groups = OnCreate.class, regexp = "[A-Za-z0-9][A-Za-z0-9_-]{0,19}",
                message = "Invalid Teacher ID. Use letters, numbers, _ or - (max 20)")
        String teacherId,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name can be at most 100 characters")
        @Pattern(regexp = "[a-zA-Z ]*", message = "Name should contain letters and spaces only")
        String name,

        @NotNull(message = "Date of birth is required (yyyy-MM-dd)")
        LocalDate dob,

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "(?i)male|female|other", message = "Gender must be Male, Female or Other")
        String gender,

        @NotBlank(message = "Qualification is required")
        @Size(max = 100, message = "Qualification can be at most 100 characters")
        String qualification,

        @NotBlank(message = "Email is required")
        @Size(max = 100, message = "Email can be at most 100 characters")
        @Pattern(regexp = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+", message = "Invalid Email ID. Please enter a valid email.")
        String email,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "[6-9][0-9]{9}", message = "Contact number must be exactly 10 digits and start with 6-9")
        String contactNumber,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address can be at most 255 characters")
        String address,

        @NotEmpty(message = "At least one subject is required")
        List<@NotBlank(message = "Subject names cannot be blank")
             @Size(max = 50, message = "Subject name can be at most 50 characters") String> subjects) {
}
