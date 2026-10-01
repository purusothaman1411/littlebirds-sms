package com.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.classes.Student;
import com.classes.Attendance;
import com.utils.InputHelper;

public class ReportOperations {

    // =========================
    // GENERATE REPORT CARD
    // =========================

    public static void generateReportCard(Scanner sc) {

        System.out.println();
        System.out.println("GENERATE REPORT CARD");
        System.out.println("---------------------");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {
            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                MarksOperations.getStudentMarks(studentId);

        System.out.println();
        System.out.println("--------------------------------");
        System.out.println("          STUDENT REPORT CARD");
        System.out.println("---------------------------------");

        System.out.println("Student ID   : " + student.getId());
        System.out.println("Student Name : " + student.getName());
        System.out.println("Age          : " + student.getAge());
        System.out.println("Gender       : " + student.getGender());
        System.out.println("Standard     : " + student.getStandard());

        // Display group only for 11th and 12th
        if (student.getStandard().equals("11") ||
                student.getStandard().equals("12")) {

            System.out.println(
                    "Group        : " + student.getGroup()
            );
        }

        System.out.println("---------------------------------");
        System.out.println("MARKS");
        System.out.println("---------------------------------");

        if (studentMarks == null || studentMarks.isEmpty()) {

            System.out.println("No Marks Found..!!");

        } else {

            int total = 0;
            boolean passed = true;

            for (String subject : studentMarks.keySet()) {

                int mark = studentMarks.get(subject);

                System.out.println(
                        subject + " : " + mark
                );

                total += mark;

                if (mark < 35) {
                    passed = false;
                }
            }

            int subjectCount = studentMarks.size();

            double average =
                    (double) total / subjectCount;

            double percentage =
                    ((double) total /
                    (subjectCount * 100)) * 100;

            String grade;

            if (percentage >= 90) {
                grade = "A+";
            }
            else if (percentage >= 80) {
                grade = "A";
            }
            else if (percentage >= 70) {
                grade = "B";
            }
            else if (percentage >= 60) {
                grade = "C";
            }
            else if (percentage >= 50) {
                grade = "D";
            }
            else {
                grade = "F";
            }

            System.out.println("---------------------------------");

            System.out.println(
                    "Total Marks : " + total
            );

            System.out.printf(
                    "Average     : %.2f%n",
                    average
            );

            System.out.printf(
                    "Percentage  : %.2f%%%n",
                    percentage
            );

            System.out.println(
                    "Grade       : " + grade
            );

            System.out.println(
                    "Result      : " +
                    (passed ? "PASS" : "FAIL")
            );
        }

        System.out.println("---------------------------------");
        System.out.println("ATTENDANCE");
        System.out.println("---------------------------------");

        Attendance attendance =
                AttendanceOperations.getAttendance(studentId);

        if (attendance == null) {

            System.out.println(
                    "No Attendance Record Found..!!"
            );

        } else {

            System.out.println(
                    "Present Days : "
                    + attendance.getPresentDays()
            );

            System.out.println(
                    "Absent Days  : "
                    + attendance.getAbsentDays()
            );

            System.out.println(
                    "Total Days   : "
                    + attendance.getTotalDays()
            );

            System.out.printf(
                    "Attendance   : %.2f%%%n",
                    attendance.getPercentage()
            );
        }

        System.out.println("--------------------------------");
        System.out.println("        END OF REPORT CARD");
        System.out.println("---------------------------------");
    }


    // =========================
    // CLASS WISE RESULTS
    // =========================

    public static void classWiseResults() {

        System.out.println();
        System.out.println("CLASS WISE RESULTS");
        System.out.println("------------------");

        ArrayList<Student> students =
                StudentOperations.getStudents();

        if (students.isEmpty()) {

            System.out.println(
                    "No Students Found..!!"
            );

            return;
        }

        HashMap<String, ArrayList<Student>> classStudents =
                new HashMap<>();

        for (Student student : students) {

            String standard =
                    student.getStandard();

            if (!classStudents.containsKey(standard)) {

                classStudents.put(
                        standard,
                        new ArrayList<Student>()
                );
            }

            classStudents
                    .get(standard)
                    .add(student);
        }

        for (String standard : classStudents.keySet()) {

            System.out.println();
            System.out.println(
                    "STANDARD : " + standard
            );

            System.out.println(
                    "---------------------------------"
            );

            ArrayList<Student> studentsInClass =
                    classStudents.get(standard);

            for (Student student : studentsInClass) {

                System.out.println(
                        "Student ID   : "
                        + student.getId()
                );

                System.out.println(
                        "Student Name : "
                        + student.getName()
                );

                if (student.getStandard().equals("11") ||
                        student.getStandard().equals("12")) {

                    System.out.println(
                            "Group        : "
                            + student.getGroup()
                    );
                }

                HashMap<String, Integer> marks =
                        MarksOperations.getStudentMarks(
                                student.getId()
                        );

                if (marks == null || marks.isEmpty()) {

                    System.out.println(
                            "Marks        : Not Available"
                    );

                } else {

                    int total = 0;
                    boolean passed = true;

                    for (int mark : marks.values()) {

                        total += mark;

                        if (mark < 35) {
                            passed = false;
                        }
                    }

                    int subjectCount =
                            marks.size();

                    double percentage =
                            (double) total
                            / (subjectCount * 100)
                            * 100;

                    System.out.println(
                            "Total Marks  : " + total
                    );

                    System.out.printf(
                            "Percentage   : %.2f%%%n",
                            percentage
                    );

                    System.out.println(
                            "Result       : "
                            + (passed
                            ? "PASS"
                            : "FAIL")
                    );
                }

                System.out.println(
                        "---------------------------------"
                );
            }
        }

        System.out.println();

        System.out.println(
                "-------------------------------"
        );

        System.out.println(
                "     END OF CLASS RESULTS"
        );

        System.out.println(
                "--------------------------------"
        );
    }
}