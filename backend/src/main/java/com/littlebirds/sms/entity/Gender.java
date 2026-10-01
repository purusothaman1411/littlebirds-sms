package com.littlebirds.sms.entity;

/** Stored in the database as "Male", "Female" or "Other" (same values the console app accepted). */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Case-insensitive, like the console app's readGender(). */
    public static Gender fromLabel(String value) {
        for (Gender g : values()) {
            if (g.label.equalsIgnoreCase(value.trim())) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid gender: " + value + ". Use Male, Female or Other.");
    }
}
