package com.operations;

import java.util.HashMap;
import java.util.Scanner;

import com.classes.Attendance;
import com.classes.Student;
import com.exceptions.StudentNotFoundException;
import com.utils.InputHelper;

public class AttendanceOperations {

    private static HashMap<String, Attendance> attendanceRecords =
            new HashMap<>();


    // =========================
    // MARK ATTENDANCE
    // =========================

    public static void markAttendance(
            Scanner sc) {

        String studentId = InputHelper.readStudentId(sc, "Enter Student Id: ");

        try {

            Student student =
                    StudentOperations.getStudentById(studentId);

            if (student == null) {

                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

            String status = InputHelper.readAttendanceStatus(sc, "Enter Attendance (P/A): ");

            Attendance attendance =
                    attendanceRecords.get(studentId);

            if (attendance == null) {

                attendance = new Attendance(studentId);

                attendanceRecords.put(
                        studentId,
                        attendance
                );
            }

            if (status.equalsIgnoreCase("P")) {

                attendance.markPresent();

                System.out.println(
                        "Attendance Marked Present..!!"
                );

            } else if (status.equalsIgnoreCase("A")) {

                attendance.markAbsent();

                System.out.println(
                        "Attendance Marked Absent..!!"
                );

            } else {

                System.out.println(
                        "Invalid Attendance..!!"
                );
            }

        }
        catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }


    // =========================
    // GET ATTENDANCE
    // =========================

    public static void getAttendance(
            Scanner sc) {

        String studentId = InputHelper.readStudentId(sc, "Enter Student Id: ");

        try {

            Student student =
                    StudentOperations.getStudentById(studentId);

            if (student == null) {

                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

            Attendance attendance =
                    attendanceRecords.get(studentId);

            if (attendance == null) {

                System.out.println(
                        "Attendance Not Found..!!"
                );

                return;
            }

            System.out.println();
            System.out.println("ATTENDANCE DETAILS");
            System.out.println("==============================");
            System.out.println(attendance);

        }
        catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }


    // =========================
    // ADD ATTENDANCE
    // =========================

    public static void addAttendance(
            String studentId,
            Attendance attendance) {

        attendanceRecords.put(
                studentId,
                attendance
        );
    }


    // =========================
    // GET ATTENDANCE BY ID
    // =========================

    public static Attendance getAttendance(
            String studentId) {

        return attendanceRecords.get(studentId);
    }


    // =========================
    // UPDATE ATTENDANCE
    // =========================

    public static void updateAttendance(
            String studentId,
            Attendance attendance) {

        attendanceRecords.put(
                studentId,
                attendance
        );
    }


    // =========================
    // GET ALL ATTENDANCE
    // =========================

    public static HashMap<String, Attendance>
            getAttendanceRecords() {

        return attendanceRecords;
    }


    // =========================
    // REMOVE ATTENDANCE
    // =========================

    public static void removeAttendance(
            String studentId) {

        attendanceRecords.remove(studentId);
    }
}