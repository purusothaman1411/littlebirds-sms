package com.littlebirds.sms.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.littlebirds.sms.entity.Role;

class PermissionMatrixTest {

    @Test
    void headmasterHasEveryPermission() {
        assertEquals(EnumSet.allOf(Permission.class), PermissionMatrix.permissionsOf(Role.HEADMASTER));
    }

    @Test
    void onlyHeadmasterManagesStaff() {
        for (Role role : Role.values()) {
            assertEquals(role == Role.HEADMASTER, PermissionMatrix.has(role, Permission.STAFF_MANAGE), role.name());
        }
    }

    @Test
    void workingStaffCanAddStudentsButNotMarksOrDelete() {
        assertTrue(PermissionMatrix.has(Role.WORKING_STAFF, Permission.STUDENT_ADD));
        assertTrue(PermissionMatrix.has(Role.WORKING_STAFF, Permission.ATTENDANCE_MARK));
        assertFalse(PermissionMatrix.has(Role.WORKING_STAFF, Permission.MARKS_ADD));
        assertFalse(PermissionMatrix.has(Role.WORKING_STAFF, Permission.STUDENT_DELETE));
        assertFalse(PermissionMatrix.has(Role.WORKING_STAFF, Permission.REPORT_CARD_VIEW));
    }

    @Test
    void subjectStaffCanEnterMarksButNotManageStudents() {
        assertTrue(PermissionMatrix.has(Role.SUBJECT_STAFF, Permission.MARKS_ADD));
        assertTrue(PermissionMatrix.has(Role.SUBJECT_STAFF, Permission.MARKS_UPDATE));
        assertTrue(PermissionMatrix.has(Role.SUBJECT_STAFF, Permission.REPORT_CARD_VIEW));
        assertFalse(PermissionMatrix.has(Role.SUBJECT_STAFF, Permission.STUDENT_ADD));
        assertFalse(PermissionMatrix.has(Role.SUBJECT_STAFF, Permission.TEACHER_ADD));
    }

    @Test
    void managementStaffIsReadOnlyPlusReportCards() {
        assertTrue(PermissionMatrix.has(Role.MANAGEMENT_STAFF, Permission.REPORT_CARD_VIEW));
        assertFalse(PermissionMatrix.has(Role.MANAGEMENT_STAFF, Permission.ATTENDANCE_MARK));
        assertFalse(PermissionMatrix.has(Role.MANAGEMENT_STAFF, Permission.MARKS_ADD));
    }

    @Test
    void authorityNamesIncludeRoleAndPermissions() {
        List<String> names = PermissionMatrix.authorityNames(Role.WORKING_STAFF);
        assertTrue(names.contains("ROLE_WORKING_STAFF"));
        assertTrue(names.contains("STUDENT_ADD"));
        assertFalse(names.contains("STAFF_MANAGE"));
    }
}
