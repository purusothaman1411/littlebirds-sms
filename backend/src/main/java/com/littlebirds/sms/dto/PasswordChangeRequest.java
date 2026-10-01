package com.littlebirds.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Body for resetting a staff member's password. */
public record PasswordChangeRequest(

        @NotBlank(message = "Password is required")
        @Pattern(regexp = ".{8,72}", message = "Password must be 8 to 72 characters")
        String password) {
}
