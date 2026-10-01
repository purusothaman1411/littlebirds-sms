package com.littlebirds.sms.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.littlebirds.sms.entity.Role;

/**
 * The single place that says what each role may do. It is the console app's StaffPermissions class,
 * plus STAFF_MANAGE (new). The subject-staff "own subjects only" rule is a business rule checked in MarksService.
 */
public final class PermissionMatrix {

    private static final Map<Role, Set<Permission>> BY_ROLE = new EnumMap<>(Role.class);

    static {
        // everyone may view students, teachers, marks, attendance and class results
        Set<Permission> views = EnumSet.of(Permission.STUDENT_VIEW, Permission.TEACHER_VIEW,
                Permission.MARKS_VIEW, Permission.ATTENDANCE_VIEW, Permission.REPORT_CLASS_VIEW);

        BY_ROLE.put(Role.HEADMASTER, EnumSet.allOf(Permission.class));

        Set<Permission> subjectStaff = EnumSet.copyOf(views);
        subjectStaff.addAll(EnumSet.of(Permission.MARKS_ADD, Permission.MARKS_UPDATE,
                Permission.ATTENDANCE_MARK, Permission.REPORT_CARD_VIEW));
        BY_ROLE.put(Role.SUBJECT_STAFF, subjectStaff);

        Set<Permission> workingStaff = EnumSet.copyOf(views);
        workingStaff.addAll(EnumSet.of(Permission.STUDENT_ADD, Permission.STUDENT_UPDATE,
                Permission.ATTENDANCE_MARK));
        BY_ROLE.put(Role.WORKING_STAFF, workingStaff);

        Set<Permission> managementStaff = EnumSet.copyOf(views);
        managementStaff.add(Permission.REPORT_CARD_VIEW);
        BY_ROLE.put(Role.MANAGEMENT_STAFF, managementStaff);
    }

    private PermissionMatrix() {
    }

    public static Set<Permission> permissionsOf(Role role) {
        return Collections.unmodifiableSet(BY_ROLE.get(role));
    }

    public static boolean has(Role role, Permission permission) {
        return BY_ROLE.get(role).contains(permission);
    }

    /** Authority strings for Spring Security: one per permission plus ROLE_<role>. */
    public static List<String> authorityNames(Role role) {
        List<String> names = new ArrayList<>();
        names.add("ROLE_" + role.name());
        for (Permission p : permissionsOf(role)) {
            names.add(p.name());
        }
        return names;
    }
}
