package com.littlebirds.sms.entity;

/** Stored in the database as "P" or "A" (same codes the console app used). */
public enum AttendanceStatus {
    PRESENT("P"),
    ABSENT("A");

    private final String code;

    AttendanceStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AttendanceStatus fromCode(String value) {
        for (AttendanceStatus s : values()) {
            if (s.code.equalsIgnoreCase(value.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid attendance status: " + value + ". Use P or A.");
    }
}
