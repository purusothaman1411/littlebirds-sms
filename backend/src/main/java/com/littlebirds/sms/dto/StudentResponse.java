package com.littlebirds.sms.dto;

import java.time.LocalDate;

import com.littlebirds.sms.entity.Student;

public record StudentResponse(
        String studentId,
        String name,
        LocalDate dob,
        int age,
        String gender,
        Integer standard,
        String group,
        String address,
        String contactNumber) {

    public static StudentResponse from(Student s) {
        return new StudentResponse(s.getStudentId(), s.getName(), s.getDob(), s.getAge(),
                s.getGender().getLabel(), s.getStandard(), s.getGroupName(), s.getAddress(), s.getContactNumber());
    }
}
