package com.classes;

public class Attendance {

    private String studentId;
    private int presentDays;
    private int absentDays;

    public Attendance(String studentId) {

        this.studentId = studentId;
        this.presentDays = 0;
        this.absentDays = 0;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void markPresent() {

        presentDays++;
    }

    public void markAbsent() {

        absentDays++;
    }

    public int getTotalDays() {

        return presentDays + absentDays;
    }

    public double getPercentage() {

        int totalDays = getTotalDays();

        if (totalDays == 0) {
            return 0;
        }

        return (presentDays * 100.0) / totalDays;
    }

    @Override
    public String toString() {

        return "Student ID      : " + studentId
                + "\nPresent Days    : " + presentDays
                + "\nAbsent Days     : " + absentDays
                + "\nTotal Days      : " + getTotalDays()
                + "\nPercentage      : " + getPercentage() + "%";
    }
}