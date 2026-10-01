package com.littlebirds.sms.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used for create and update. On update the ID comes from the URL; a blank password keeps the current one.
 * Subjects are names from the subjects table; "ALL" or an empty list means no subject restriction.
 */
public record StaffRequest(

        @NotBlank(groups = OnCreate.class, message = "Staff ID is required")
        @Pattern(groups = OnCreate.class, regexp = "[A-Za-z0-9][A-Za-z0-9_-]{0,19}",
                message = "Invalid Staff ID. Use letters, numbers, _ or - (max 20)")
        String staffId,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name can be at most 100 characters")
        String name,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3 to 50 characters")
        @Pattern(regexp = "\\S*", message = "Username cannot contain spaces")
        String username,

        @NotBlank(groups = OnCreate.class, message = "Password is required")
        @Pattern(regexp = "|.{8,72}", message = "Password must be 8 to 72 characters")
        String password,

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "(?i)HEADMASTER|SUBJECT_STAFF|WORKING_STAFF|MANAGEMENT_STAFF",
                message = "Role must be HEADMASTER, SUBJECT_STAFF, WORKING_STAFF or MANAGEMENT_STAFF")
        String role,

        @Pattern(regexp = "(?i)|ALL|1-10|11-12", message = "Class range must be ALL, 1-10 or 11-12")
        String classRange,

        String group,

        List<@Size(max = 50, message = "Subject name can be at most 50 characters") String> subjects) {
}
