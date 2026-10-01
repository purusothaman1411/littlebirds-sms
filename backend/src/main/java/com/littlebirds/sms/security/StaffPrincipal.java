package com.littlebirds.sms.security;

import com.littlebirds.sms.entity.Role;

/** The logged-in staff member, available in controllers with @AuthenticationPrincipal. */
public record StaffPrincipal(String staffId, String username, String name, Role role) {
}
