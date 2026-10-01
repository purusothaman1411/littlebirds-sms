package com.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.classes.Teacher;
import com.exceptions.TeacherAlreadyExistsException;
import com.exceptions.TeacherNotFoundException;
import com.filehandling.TeacherFileHandler;
import com.utils.InputHelper;

public class TeacherOperations {

    private static ArrayList<Teacher> teachers = new ArrayList<>();

    static {
        teachers = TeacherFileHandler.loadTeachers();
    }

    private static void persistTeachers() {
        File file = new File("data/teachers.txt");

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Teacher teacher : teachers) {
                String subjects = "";

                if (teacher.getSubjects() != null) {
                    subjects = String.join(",", teacher.getSubjects());
                }

                String data = teacher.getId() + "|" +
                        teacher.getName() + "|" +
                        teacher.getDob() + "|" +
                        teacher.getGender() + "|" +
                        teacher.getQualification() + "|" +
                        teacher.getEmail() + "|" +
                        teacher.getContact() + "|" +
                        teacher.getAddress() + "|" +
                        subjects;

                bw.write(data);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving teacher data: " + e.getMessage());
        }
    }


    // ============================================================
    // ADD TEACHER
    // ============================================================

    public static void addTeacher(Scanner sc) {

        try {

            // Consume newline left by menu nextInt()
            sc.nextLine();


            // ----------------------------------------------------
            // TEACHER ID
            // ----------------------------------------------------

            String id = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher ID : "
            );


            // Check duplicate Teacher ID
            if (getTeacherById(id) != null) {

                throw new TeacherAlreadyExistsException(
                        "Teacher ID already exists..!!"
                );
            }


            // ----------------------------------------------------
            // TEACHER NAME
            // ----------------------------------------------------

            String name = InputHelper.readName(
                    sc,
                    "Enter Teacher Name : "
            );


            // ----------------------------------------------------
            // DOB
            // ----------------------------------------------------

            String dob = InputHelper.readDate(
                    sc,
                    "Enter DOB (DD-MM-YYYY) : "
            );


            // ----------------------------------------------------
            // GENDER
            // ----------------------------------------------------

            String gender = InputHelper.readGender(
                    sc,
                    "Enter Teacher Gender : "
            );


            // ----------------------------------------------------
            // QUALIFICATION
            // ----------------------------------------------------

            String qualification = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher Qualification : "
            );


            // ----------------------------------------------------
            // EMAIL
            // ----------------------------------------------------

            String email = InputHelper.readEmail(
                    sc,
                    "Enter Email ID : "
            );


            // ----------------------------------------------------
            // CONTACT NUMBER
            // ----------------------------------------------------

            String contact = InputHelper.readMobile(
                    sc,
                    "Enter Contact Number : "
            );


            // ----------------------------------------------------
            // ADDRESS
            // ----------------------------------------------------

            String address = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher Address : "
            );


            // ----------------------------------------------------
            // SUBJECTS
            // ----------------------------------------------------

            ArrayList<String> subjects = new ArrayList<>();

            int count = InputHelper.readPositiveInt(
                    sc,
                    "Enter Number of Subjects : "
            );

            sc.nextLine();


            for (int i = 0; i < count; i++) {

                String subject = InputHelper.readRequiredString(
                        sc,
                        "Enter Subject " + (i + 1) + " : "
                );

                subjects.add(subject);
            }


            // ----------------------------------------------------
            // CREATE TEACHER OBJECT
            // ----------------------------------------------------

            Teacher teacher = new Teacher(
                    id,
                    name,
                    dob,
                    gender,
                    qualification,
                    email,
                    contact,
                    address,
                    subjects
            );


            // Add to ArrayList
            teachers.add(teacher);


            // Save to file
            TeacherFileHandler.saveTeacher(teacher);


            // ----------------------------------------------------
            // SUCCESS MESSAGE
            // ----------------------------------------------------

            System.out.println();

            System.out.println(
                    "Teacher Added Successfully..!!"
            );

            System.out.println(
                    "-------------------------------------"
            );

            System.out.println(
                    "Total Teachers : " + teachers.size()
            );

        } catch (TeacherAlreadyExistsException e) {

            System.out.println(e.getMessage());
        }
    }


    // ============================================================
    // GET TEACHER
    // ============================================================

    public static void getTeacher(Scanner sc) {

        try {

            sc.nextLine();

            String id = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher ID : "
            );


            Teacher teacher = getTeacherById(id);


            if (teacher == null) {

                throw new TeacherNotFoundException(
                        "Teacher Not Found..!!"
                );
            }


            System.out.println();

            System.out.println(teacher);


        } catch (TeacherNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }


    // ============================================================
    // GET ALL TEACHERS
    // ============================================================

    public static void getAllTeachers() {

        if (teachers.isEmpty()) {

            System.out.println(
                    "No Teachers Found..!!"
            );

            return;
        }


        System.out.println();

        System.out.println(
                "========== ALL TEACHERS =========="
        );


        for (Teacher teacher : teachers) {

            System.out.println(teacher);

            System.out.println(
                    "----------------------------------"
            );
        }
    }


    // ============================================================
    // UPDATE TEACHER
    // ============================================================

    public static void updateTeacher(Scanner sc) {

        try {

            sc.nextLine();


            // ----------------------------------------------------
            // TEACHER ID
            // ----------------------------------------------------

            String id = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher ID : "
            );


            Teacher teacher = getTeacherById(id);


            if (teacher == null) {

                throw new TeacherNotFoundException(
                        "Teacher Not Found..!!"
                );
            }


            // ----------------------------------------------------
            // TEACHER NAME
            // ----------------------------------------------------

            String name = InputHelper.readName(
                    sc,
                    "Enter Teacher Name : "
            );

            teacher.setName(name);


            // ----------------------------------------------------
            // DOB
            // ----------------------------------------------------

            String dob = InputHelper.readDate(
                    sc,
                    "Enter DOB (DD-MM-YYYY) : "
            );

            teacher.setDob(dob);


            // ----------------------------------------------------
            // GENDER
            // ----------------------------------------------------

            String gender = InputHelper.readGender(
                    sc,
                    "Enter Teacher Gender : "
            );

            teacher.setGender(gender);


            // ----------------------------------------------------
            // QUALIFICATION
            // ----------------------------------------------------

            String qualification = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher Qualification : "
            );

            teacher.setQualification(qualification);


            // ----------------------------------------------------
            // EMAIL
            // ----------------------------------------------------

            String email = InputHelper.readEmail(
                    sc,
                    "Enter Email ID : "
            );

            teacher.setEmail(email);


            // ----------------------------------------------------
            // CONTACT NUMBER
            // ----------------------------------------------------

            String contact = InputHelper.readMobile(
                    sc,
                    "Enter Contact Number : "
            );

            teacher.setContact(contact);


            // ----------------------------------------------------
            // ADDRESS
            // ----------------------------------------------------

            String address = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher Address : "
            );

            teacher.setAddress(address);


            // ----------------------------------------------------
            // SUBJECTS
            // ----------------------------------------------------

            int count = InputHelper.readPositiveInt(
                    sc,
                    "Enter Number of Subjects : "
            );

            sc.nextLine();


            ArrayList<String> subjects = new ArrayList<>();


            for (int i = 0; i < count; i++) {

                String subject = InputHelper.readRequiredString(
                        sc,
                        "Enter Subject " + (i + 1) + " : "
                );

                subjects.add(subject);
            }


            teacher.setSubjects(subjects);

            persistTeachers();

            // ----------------------------------------------------
            // SUCCESS MESSAGE
            // ----------------------------------------------------

            System.out.println();

            System.out.println(
                    "Teacher Updated Successfully..!!"
            );


        } catch (TeacherNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }


    // ============================================================
    // DELETE TEACHER
    // ============================================================

    public static void deleteTeacher(Scanner sc) {

        try {

            sc.nextLine();


            String id = InputHelper.readRequiredString(
                    sc,
                    "Enter Teacher ID : "
            );


            Teacher teacher = getTeacherById(id);


            if (teacher == null) {

                throw new TeacherNotFoundException(
                        "Teacher Not Found..!!"
                );
            }


            teachers.remove(teacher);

            persistTeachers();

            System.out.println(
                    "Teacher Deleted Successfully..!!"
            );


        } catch (TeacherNotFoundException e) {

            System.out.println(e.getMessage());
        }
    }


    // ============================================================
    // SEARCH TEACHER BY SUBJECT
    // ============================================================

    public static void searchTeacherBySubject(Scanner sc) {

        sc.nextLine();


        String subject = InputHelper.readRequiredString(
                sc,
                "Enter Subject : "
        );


        boolean found = false;


        for (Teacher teacher : teachers) {

            ArrayList<String> subjects =
                    teacher.getSubjects();


            if (subjects != null) {

                for (String teacherSubject : subjects) {

                    if (teacherSubject.equalsIgnoreCase(subject)) {

                        System.out.println();

                        System.out.println(teacher);

                        found = true;

                        break;
                    }
                }
            }
        }


        if (!found) {

            System.out.println(
                    "No Teacher Found for this Subject..!!"
            );
        }
    }


    // ============================================================
    // TEACHERS MORE THAN TWO SUBJECTS
    // ============================================================

    public static void teachersMoreThanTwoSubjects() {

        boolean found = false;


        for (Teacher teacher : teachers) {

            if (teacher.getSubjects() != null
                    && teacher.getSubjects().size() > 2) {

                System.out.println();

                System.out.println(teacher);

                System.out.println(
                        "----------------------------------"
                );

                found = true;
            }
        }


        if (!found) {

            System.out.println(
                    "No Teacher Found..!!"
            );
        }
    }


    // ============================================================
    // GET TEACHER BY ID
    // ============================================================

    public static Teacher getTeacherById(String id) {

        for (Teacher teacher : teachers) {

            if (teacher.getId().equalsIgnoreCase(id)) {

                return teacher;
            }
        }


        return null;
    }


    // ============================================================
    // GET TEACHERS LIST
    // ============================================================

    public static ArrayList<Teacher> getTeachers() {

        return teachers;
    }
}