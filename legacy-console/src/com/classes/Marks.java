package com.classes;

import java.util.HashMap;

public class Marks {

    private String studentId;
    private HashMap<String, Integer> marks;

    public Marks(String studentId) {

        this.studentId = studentId;
        marks = new HashMap<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public HashMap<String, Integer> getMarks() {
        return marks;
    }

    public void addMark(String subject, int mark) {

        marks.put(subject, mark);
    }

    public void updateMark(String subject, int mark) {

        marks.put(subject, mark);
    }

    public Integer getMark(String subject) {

        return marks.get(subject);
    }
}