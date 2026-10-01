package com.filehandling;

import com.classes.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StudentFileHandler {

    private static final String FILE_PATH = "data/students.txt";

    public static ArrayList<Student> loadStudents() {

        ArrayList<Student> students = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return students;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length < 9) {
                    continue;
                }

                try {
                    String id = data[0];
                    String name = data[1];
                    String dob = data[2];
                    int age = Integer.parseInt(data[3]);
                    String gender = data[4];
                    String standard = data[5];
                    String address = data[6];
                    String contactNumber = data[7];
                    String group = data[8];

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
                } catch (NumberFormatException e) {
                    System.out.println("Error loading student data: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading student data: " + e.getMessage());
        }

        return students;
    }

    public static void saveStudents(ArrayList<Student> students) {

        File file = new File(FILE_PATH);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {

            if (students == null) {
                return;
            }

            for (Student student : students) {

                if (student == null) {
                    continue;
                }

                String data = student.getId() + "|" +
                        student.getName() + "|" +
                        student.getDob() + "|" +
                        student.getAge() + "|" +
                        student.getGender() + "|" +
                        student.getStandard() + "|" +
                        student.getAddress() + "|" +
                        student.getContactNumber() + "|" +
                        student.getGroup();

                bw.write(data);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving student data: " + e.getMessage());
        }
    }

    public static Student getStudentById(ArrayList<Student> students, String studentId) {

        if (students == null || studentId == null) {
            return null;
        }

        for (Student student : students) {

            if (student != null && student.getId().equalsIgnoreCase(studentId)) {
                return student;
            }
        }

        return null;
    }

    public static void updateStudent(ArrayList<Student> students, Student updatedStudent) {

        if (students == null || updatedStudent == null) {
            return;
        }

        for (int i = 0; i < students.size(); i++) {

            if (students.get(i) != null && students.get(i).getId().equalsIgnoreCase(updatedStudent.getId())) {
                students.set(i, updatedStudent);
                return;
            }
        }
    }

    public static void deleteStudent(ArrayList<Student> students, String studentId) {

        if (students == null || studentId == null) {
            return;
        }

        for (int i = 0; i < students.size(); i++) {

            if (students.get(i) != null && students.get(i).getId().equalsIgnoreCase(studentId)) {
                students.remove(i);
                return;
            }
        }
    }
}
