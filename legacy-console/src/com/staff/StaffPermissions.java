
package com.staff;

public class StaffPermissions {

    public static boolean canAddStudent(Role role) {
        return role == Role.HEADMASTER
                || role == Role.WORKING_STAFF;
    }

    public static boolean canViewStudent(Role role) {
        return true;
    }

    public static boolean canUpdateStudent(Role role) {
        return role == Role.HEADMASTER
                || role == Role.WORKING_STAFF;
    }

    public static boolean canDeleteStudent(Role role) {
        return role == Role.HEADMASTER;
    }

    public static boolean canAddTeacher(Role role) {
        return role == Role.HEADMASTER;
    }

    public static boolean canViewTeacher(Role role) {
        return true;
    }

    public static boolean canUpdateTeacher(Role role) {
        return role == Role.HEADMASTER;
    }

    public static boolean canDeleteTeacher(Role role) {
        return role == Role.HEADMASTER;
    }

    public static boolean canAddMarks(Role role) {
        return role == Role.HEADMASTER
                || role == Role.SUBJECT_STAFF;
    }

    public static boolean canUpdateMarks(Role role) {
        return role == Role.HEADMASTER
                || role == Role.SUBJECT_STAFF;
    }

    public static boolean canViewMarks(Role role) {
        return true;
    }

    public static boolean canMarkAttendance(Role role) {
        return role == Role.HEADMASTER
                || role == Role.SUBJECT_STAFF
                || role == Role.WORKING_STAFF;
    }

    public static boolean canViewAttendance(Role role) {
        return true;
    }

    public static boolean canViewReports(Role role) {
        return true;
    }

    public static boolean canViewReportCard(Role role) {
        return role == Role.HEADMASTER
                || role == Role.SUBJECT_STAFF
                || role == Role.MANAGEMENT_STAFF;
    }


    // =====================================================
    // MENU ACCESS PERMISSIONS
    // =====================================================

    public static boolean canAccessStudentMenu(Role role) {

        return canViewStudent(role)
                || canAddStudent(role)
                || canUpdateStudent(role)
                || canDeleteStudent(role);
    }


    public static boolean canAccessTeacherMenu(Role role) {

        return canViewTeacher(role)
                || canAddTeacher(role)
                || canUpdateTeacher(role)
                || canDeleteTeacher(role);
    }


    public static boolean canAccessMarksMenu(Role role) {

        return canViewMarks(role)
                || canAddMarks(role)
                || canUpdateMarks(role);
    }


    public static boolean canAccessAttendanceMenu(Role role) {

        return canViewAttendance(role)
                || canMarkAttendance(role);
    }


    public static boolean canAccessReportMenu(Role role) {

        return canViewReports(role)
                || canViewReportCard(role);
    }
}
