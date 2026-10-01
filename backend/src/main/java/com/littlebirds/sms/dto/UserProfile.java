package com.littlebirds.sms.dto;

import java.util.List;

import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.security.Permission;
import com.littlebirds.sms.security.PermissionMatrix;

/**
 * The logged-in user. "permissions" lets the frontend show only the menus this role may use
 * (the backend still enforces every rule itself).
 */
public record UserProfile(String staffId, String name, String username, String role, List<String> permissions) {

    public static UserProfile from(StaffUser user) {
        List<String> permissions = PermissionMatrix.permissionsOf(user.getRole()).stream()
                .map(Permission::name)
                .sorted()
                .toList();
        return new UserProfile(user.getStaffId(), user.getName(), user.getUsername(), user.getRole().name(), permissions);
    }
}
