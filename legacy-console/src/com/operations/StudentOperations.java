package com.operations;

import com.exceptions.StudentNotFoundException;
import com.exceptions.StudentAlreadyExistsException;

import java.util.ArrayList;
import java.util.Scanner;

import com.classes.Student;
import com.filehandling.StudentFileHandler;
import com.utils.InputHelper;

public class StudentOperations {

    private static ArrayList<Student> students = new ArrayList<>();

    static {
        students = StudentFileHandler.loadStudents();
    }


    // =========================================================
    // ADD STUDENT
    // =========================================================

    public static void addStudent(Scanner sc) {

        String id = InputHelper.readStudentId(sc, "Enter Student ID : ");

        // Check duplicate Student ID
        try {

            if (getStudentById(id) != null) {

                throw new StudentAlreadyExistsException(
                        "Student ID already exists!"
                );
            }

        } catch (StudentAlreadyExistsException e) {

            System.out.println(e.getMessage());
            return;
        }


        sc.nextLine();

        String name = InputHelper.readName(sc, "Enter Student Name : ");

        String dob = InputHelper.readDate(sc, "Enter Student DOB : ");

        int age = InputHelper.readAge(sc, "Enter Age : ");
        sc.nextLine();

        String gender = InputHelper.readGender(sc, "Enter Gender : ");

        String standard = InputHelper.readStandard(sc, "Enter Standard : ");

        String group = "";


        // =====================================================
        // GROUP FOR 11th / 12th
        // =====================================================

        if (standard.equals("11") || standard.equals("12")) {

            System.out.println();
            System.out.println("Select Group");
            System.out.println("==============================");
            System.out.println("1. Bio-Maths");
            System.out.println("2. Computer Science");
            System.out.println("3. Commerce");
            System.out.println("4. Humanities");

            int groupOption = InputHelper.readChoice(sc, "Choose Group : ", 1, 4);
            sc.nextLine();

            switch (groupOption) {

                case 1:
                    group = "Bio-Maths";
                    break;

                case 2:
                    group = "Computer Science";
                    break;

                case 3:
                    group = "Commerce";
                    break;

                case 4:
                    group = "Humanities";
                    break;

                default:
                    System.out.println("Invalid Group..!!");
                    return;
            }
        }


        // =====================================================
        // ADDRESS & CONTACT
        // =====================================================

        String address = InputHelper.readRequiredString(sc, "Enter Address : ");

        String contactNumber = InputHelper.readMobile(sc, "Enter Contact Number : ");


        // =====================================================
        // CREATE STUDENT
        // =====================================================

        Student student = new Student(
                id,
                name,
                dob,
                age,
                gender,
                standard,
                address,
                contactNumber,
                group
        );

        students.add(student);
        StudentFileHandler.saveStudents(students);

        System.out.println();
        System.out.println("Student Added Successfully..!!");
        System.out.println("----------------------------------");
    }


    // =========================================================
    // GET STUDENT
    // =========================================================

    public static void getStudent(Scanner sc) {

        String id = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student = getStudentById(id);

        try {

            if (student == null) {

                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

        } catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
            return;
        }

        System.out.println();
        System.out.println("STUDENT DETAILS");
        System.out.println("==============================");
        System.out.println(student);
    }


    // =========================================================
    // GET ALL STUDENTS
    // =========================================================

    public static void getAllStudents() {

        if (students.isEmpty()) {

            System.out.println("No Students Found..!!");
            return;
        }

        System.out.println();
        System.out.println("ALL STUDENTS");
        System.out.println("==============================");

        for (Student student : students) {

            System.out.println(student);
            System.out.println("------------------------------");
        }
    }


    // =========================================================
    // UPDATE STUDENT
    // =========================================================

    public static void updateStudent(Scanner sc) {

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");
        sc.nextLine();

        // FIXED: studentId instead of id
        Student student = getStudentById(studentId);

        try {

            if (student == null) {

                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

        } catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
            return;
        }


        String name = InputHelper.readName(sc, "Enter Student Name : ");

        String dob = InputHelper.readDate(sc, "Enter Student DOB : ");

        int age = InputHelper.readAge(sc, "Enter Student Age : ");
        sc.nextLine();

        String gender = InputHelper.readGender(sc, "Enter Gender : ");

        String standard = InputHelper.readStandard(sc, "Enter Standard : ");

        String group = "";


        // =====================================================
        // GROUP FOR 11th / 12th
        // =====================================================

        if (standard.equals("11") || standard.equals("12")) {

            System.out.println();
            System.out.println("Select Group");
            System.out.println("==============================");
            System.out.println("1. Bio-Maths");
            System.out.println("2. Computer Science");
            System.out.println("3. Commerce");
            System.out.println("4. Humanities");

            int groupOption = InputHelper.readChoice(sc, "Choose Group : ", 1, 4);
            sc.nextLine();

            switch (groupOption) {

                case 1:
                    group = "Bio-Maths";
                    break;

                case 2:
                    group = "Computer Science";
                    break;

                case 3:
                    group = "Commerce";
                    break;

                case 4:
                    group = "Humanities";
                    break;

                default:
                    System.out.println("Invalid Group..!!");
                    return;
            }
        }


        // =====================================================
        // ADDRESS & CONTACT
        // =====================================================

        String address = InputHelper.readRequiredString(sc, "Enter Address : ");

        String contactNumber = InputHelper.readMobile(sc, "Enter Contact Number : ");


        // =====================================================
        // UPDATE STUDENT OBJECT
        // =====================================================

        student.setName(name);
        student.setDob(dob);
        student.setAge(age);
        student.setGender(gender);
        student.setStandard(standard);
        student.setGroup(group);
        student.setAddress(address);
        student.setContactNumber(contactNumber);

        StudentFileHandler.saveStudents(students);

        System.out.println();
        System.out.println("Student Updated Successfully..!!");
    }


    // =========================================================
    // DELETE STUDENT
    // =========================================================

    public static void deleteStudent(Scanner sc) {

        String id = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student = getStudentById(id);

        try {

            if (student == null) {

                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

        } catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());
            return;
        }


        students.remove(student);
        StudentFileHandler.saveStudents(students);

        MarksOperations.removeMarks(id);
        AttendanceOperations.removeAttendance(id);

        System.out.println();
        System.out.println("Student Deleted Successfully..!!");
    }


    // =========================================================
    // SEARCH STUDENT
    // =========================================================

    public static void searchStudent(Scanner sc) {

        sc.nextLine();

        String name = InputHelper.readName(sc, "Enter Student Name : ");

        boolean found = false;

        for (Student student : students) {

            if (student.getName().equalsIgnoreCase(name)) {

                System.out.println();
                System.out.println("STUDENT FOUND");
                System.out.println("==============================");
                System.out.println(student);

                found = true;
            }
        }

        // No custom exception needed here
        if (!found) {

            System.out.println("Student Not Found..!!");
        }
    }


    // =========================================================
    // GET STUDENT BY ID
    // =========================================================

    public static Student getStudentById(String id) {

        for (Student student : students) {

            if (student.getId().equalsIgnoreCase(id)) {

                return student;
            }
        }

        return null;
    }


    // =========================================================
    // GET STUDENTS
    // =========================================================

    public static ArrayList<Student> getStudents() {

        return students;
    }
}