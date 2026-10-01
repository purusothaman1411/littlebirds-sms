package com.littlebirds.sms.security;

/** Every action the API protects. Names are used as Spring Security authorities. */
public enum Permission {
    STUDENT_VIEW, STUDENT_ADD, STUDENT_UPDATE, STUDENT_DELETE,
    TEACHER_VIEW, TEACHER_ADD, TEACHER_UPDATE, TEACHER_DELETE,
    MARKS_VIEW, MARKS_ADD, MARKS_UPDATE,
    ATTENDANCE_VIEW, ATTENDANCE_MARK,
    REPORT_CLASS_VIEW, REPORT_CARD_VIEW,
    STAFF_MANAGE
}
