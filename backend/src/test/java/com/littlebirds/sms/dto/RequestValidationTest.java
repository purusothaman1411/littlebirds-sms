package com.littlebirds.sms.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Checks the request rules taken from the console app's InputHelper (plain Bean Validation, no Spring needed). */
class RequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final LocalDate DOB = LocalDate.of(2010, 5, 1);

    private <T> Set<String> failingFields(T target, Class<?>... groups) {
        return validator.validate(target, groups).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private static StudentRequest student(String id, String name, String gender, Integer standard, String phone) {
        return new StudentRequest(id, name, DOB, gender, standard, null, "12 Main Road", phone);
    }

    @Test
    void validStudentPasses() {
        assertTrue(failingFields(student("S101", "Arun Kumar", "male", 5, "9876543210"), OnCreate.class).isEmpty());
    }

    @Test
    void studentIdRuleAppliesOnlyOnCreate() {
        StudentRequest noId = student(null, "Arun Kumar", "Male", 5, "9876543210");
        assertTrue(failingFields(noId).isEmpty());                                        // update: ID comes from URL
        assertTrue(failingFields(noId, OnCreate.class).contains("studentId"));            // create: required
        assertTrue(failingFields(student("101", "Arun", "Male", 5, "9876543210"), OnCreate.class)
                .contains("studentId"));                                                  // must start with a letter
    }

    @Test
    void invalidStudentFieldsAreReported() {
        Set<String> bad = failingFields(student("S1", "Arun 2", "x", 13, "12345"), OnCreate.class);
        assertTrue(bad.containsAll(Set.of("name", "gender", "standard", "contactNumber")));
    }

    @Test
    void teacherRules() {
        TeacherRequest bad = new TeacherRequest("T1", "Virat", DOB, "Male", "M.Sc Maths", "not-an-email",
                "9887456523", "Namakkal", List.of());
        Set<String> fields = failingFields(bad, OnCreate.class);
        assertTrue(fields.contains("email"));
        assertTrue(fields.contains("subjects"));

        TeacherRequest blankSubject = new TeacherRequest("T1", "Virat", DOB, "Male", "M.Sc Maths", "v@school.in",
                "9887456523", "Namakkal", List.of("Maths", " "));
        assertTrue(failingFields(blankSubject, OnCreate.class).stream().anyMatch(f -> f.startsWith("subjects")));

        TeacherRequest good = new TeacherRequest("110", "Virat", DOB, "Male", "M.Sc Maths", "v@school.in",
                "9887456523", "Namakkal", List.of("Maths"));
        assertTrue(failingFields(good, OnCreate.class).isEmpty());
    }

    @Test
    void staffPasswordRules() {
        StaffRequest shortPassword = new StaffRequest("ST20", "Anu", "anu", "short", "SUBJECT_STAFF",
                "ALL", null, List.of("Tamil"));
        assertTrue(failingFields(shortPassword, OnCreate.class).contains("password"));

        StaffRequest blankOnUpdate = new StaffRequest(null, "Anu", "anu", "", "SUBJECT_STAFF",
                "ALL", null, List.of("Tamil"));
        assertTrue(failingFields(blankOnUpdate).isEmpty());                               // blank keeps current password
        assertTrue(failingFields(blankOnUpdate, OnCreate.class).contains("password"));    // but create needs one
    }

    @Test
    void marksMustBeBetween0And100() {
        assertTrue(failingFields(new MarksRequest(Map.of("English", 101))).stream().anyMatch(f -> f.startsWith("marks")));
        assertTrue(failingFields(new MarksRequest(Map.of("English", -1))).stream().anyMatch(f -> f.startsWith("marks")));
        assertTrue(failingFields(new MarksRequest(Map.of())).contains("marks"));
        assertTrue(failingFields(new MarksRequest(Map.of("English", 100, "Tamil", 0))).isEmpty());
    }

    @Test
    void attendanceEntriesAreValidatedToo() {
        AttendanceRequest bad = new AttendanceRequest(LocalDate.of(2026, 9, 1), 5,
                List.of(new AttendanceEntry("s101", "P"), new AttendanceEntry("s102", "X")));
        assertTrue(failingFields(bad).stream().anyMatch(f -> f.startsWith("entries")));

        AttendanceRequest good = new AttendanceRequest(LocalDate.of(2026, 9, 1), 5,
                List.of(new AttendanceEntry("s101", "p"), new AttendanceEntry("s102", "A")));
        assertTrue(failingFields(good).isEmpty());
    }
}
