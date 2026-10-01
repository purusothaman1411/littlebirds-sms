package com.littlebirds.sms.dto;

import java.time.LocalDate;
import java.util.List;

import com.littlebirds.sms.entity.Teacher;

public record TeacherResponse(
        String teacherId,
        String name,
        LocalDate dob,
        String gender,
        String qualification,
        String email,
        String contactNumber,
        String address,
        List<String> subjects) {

    public static TeacherResponse from(Teacher t) {
        List<String> subjects = t.getSubjects().stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        return new TeacherResponse(t.getTeacherId(), t.getName(), t.getDob(), t.getGender().getLabel(),
                t.getQualification(), t.getEmail(), t.getContactNumber(), t.getAddress(), subjects);
    }
}
