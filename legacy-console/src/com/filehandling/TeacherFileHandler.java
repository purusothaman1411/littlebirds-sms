package com.filehandling;

import com.classes.Teacher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TeacherFileHandler {

    private static final String FILE_PATH = "data/teachers.txt";


    // =========================================================
    // SAVE TEACHER
    // =========================================================

    public static void saveTeacher(Teacher teacher) {

        try {

            File file = new File(FILE_PATH);

            // Create data folder if it does not exist
            file.getParentFile().mkdirs();

            try (BufferedWriter bw = new BufferedWriter(
                    new FileWriter(file, true))) {

                String subjects = "";

                if (teacher.getSubjects() != null) {
                    subjects = String.join(",", teacher.getSubjects());
                }

                String data =
                        teacher.getId() + "|" +
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

            System.out.println(
                    "Error saving teacher data: " + e.getMessage()
            );
        }
    }



    // =========================================================
    // DELETE TEACHERS
    // =========================================================
       /* public static ArrayList<Teacher> removeTeacher(){
            ArrayList<Teacher> teachers = new ArrayList<>();

            if(!file.exists()){
                return teachers;
            }
            try{
                System.out.println("nothing");
            }catch{
                System.out.println("its happens");
            }
        }*/

    // =========================================================
    // LOAD ALL TEACHERS
    // =========================================================

    public static ArrayList<Teacher> loadTeachers() {

        ArrayList<Teacher> teachers = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return teachers;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                if (data.length < 9) {
                    continue;
                }

                String id = data[0];
                String name = data[1];
                String dob = data[2];
                String gender = data[3];
                String qualification = data[4];
                String email = data[5];
                String contact = data[6];
                String address = data[7];

                ArrayList<String> subjects = new ArrayList<>();

                if (!data[8].trim().isEmpty()) {

                    String[] subjectList = data[8].split(",");

                    for (String subject : subjectList) {
                        subjects.add(subject.trim());
                    }
                }

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

                teachers.add(teacher);
            }

        } catch (IOException | NumberFormatException e) {

            System.out.println(
                    "Error loading teacher data: " + e.getMessage()
            );
        }

        return teachers;
    }
}