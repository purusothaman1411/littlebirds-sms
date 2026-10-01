package com.littlebirds.sms.dto;

import java.util.List;

import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.entity.Subject;

/** Never contains the password or its hash. */
public record StaffResponse(
        String staffId,
        String name,
        String username,
        String role,
        String classRange,
        String group,
        List<String> subjects) {

    public static StaffResponse from(StaffUser u) {
        List<String> subjects = u.getSubjects().stream()
                .map(Subject::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        return new StaffResponse(u.getStaffId(), u.getName(), u.getUsername(), u.getRole().name(),
                u.getClassRange(), u.getGroupName(), subjects);
    }
}
