package com.operations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import com.authentication.LoginManager;
import com.classes.Student;
import com.exceptions.StudentNotFoundException;
import com.exceptions.InvalidMarkException;
import com.staff.Role;
import com.staff.Staff;
import com.utils.InputHelper;

public class MarksOperations {

    // MARKS STORE
    private static HashMap<String, HashMap<String, Integer>> marks =
            new HashMap<>();

    private static ArrayList<String> getAllowedSubjectsForCurrentStaff(ArrayList<String> subjects) {
        Staff currentStaff = LoginManager.getCurrentStaff();

        if (currentStaff == null || currentStaff.getRole() != Role.SUBJECT_STAFF) {
            return subjects;
        }

        ArrayList<String> assignedSubjects = currentStaff.getSubjects();
        ArrayList<String> allowedSubjects = new ArrayList<>();

        if (assignedSubjects == null || assignedSubjects.isEmpty()) {
            return allowedSubjects;
        }

        for (String subject : subjects) {
            for (String assignedSubject : assignedSubjects) {
                if (subject.equalsIgnoreCase(assignedSubject.trim())) {
                    allowedSubjects.add(subject);
                    break;
                }
            }
        }

        return allowedSubjects;
    }


    // ADD MARKS
    public static void addMarks(Scanner sc) {

        System.out.println();
        System.out.println("ADD MARKS");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        try {

            Student student =
                    StudentOperations.getStudentById(studentId);

            if (student == null) {
                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

            String standard = student.getStandard();
            String group = student.getGroup();

            ArrayList<String> subjects =
                    getSubjects(standard, group);

            Staff currentStaff = LoginManager.getCurrentStaff();
            if (currentStaff != null && currentStaff.getRole() == Role.SUBJECT_STAFF) {
                subjects = getAllowedSubjectsForCurrentStaff(subjects);

                if (subjects.isEmpty()) {
                    System.out.println("Access Denied..!! You are not assigned to any valid subject for this student.");
                    return;
                }
            }

            // Display subjects
            System.out.println();
            System.out.println("Subjects");
            System.out.println("------------------------------");

            for (String subject : subjects) {
                System.out.println("- " + subject);
            }

            System.out.println("------------------------------");

            if (!marks.containsKey(studentId)) {

                marks.put(
                        studentId,
                        new HashMap<String, Integer>()
                );
            }

            for (String subject : subjects) {

                int mark = InputHelper.readMark(sc, "Enter " + subject + " Mark : ");

                if (mark < 0 || mark > 100) {

                    throw new InvalidMarkException(
                            "Invalid Mark..!! Mark should be between 0 and 100."
                    );
                }

                marks.get(studentId).put(
                        subject,
                        mark
                );
            }

            System.out.println();

            int total = 0;

            for (String subject : subjects) {

                total += marks.get(studentId).get(subject);
            }

            double percentage =
                    ((double) total / (subjects.size() * 100)) * 100;

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

            boolean passed = true;

            for (String subject : subjects) {

                int mark =
                        marks.get(studentId).get(subject);

                if (mark < 35) {

                    passed = false;
                    break;
                }
            }

            System.out.println();
            System.out.println("Marks Added Successfully..!!");

            System.out.println();
            System.out.println("Total Marks : " + total);

            System.out.printf(
                    "Percentage  : %.2f%%%n",
                    percentage
            );

            System.out.println(
                    "Result      : " +
                    (passed ? "PASS" : "FAIL")
            );

        }
        catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());

        }
        catch (InvalidMarkException e) {

            System.out.println(e.getMessage());
        }
    }


    // GET SUBJECTS
    private static ArrayList<String> getSubjects(
            String standard,
            String group) {

        ArrayList<String> subjects =
                new ArrayList<>();

        // 1st to 10th
        if (!standard.equals("11") &&
                !standard.equals("12")) {

            subjects.add("Tamil");
            subjects.add("English");
            subjects.add("Mathematics");
            subjects.add("Science");
            subjects.add("Social Science");

            return subjects;
        }

        // Bio Maths
        if (group.equalsIgnoreCase("Bio-Maths")) {

            subjects.add("Tamil");
            subjects.add("English");
            subjects.add("Physics");
            subjects.add("Chemistry");
            subjects.add("Mathematics");
            subjects.add("Biology");
        }

        // Computer Science
        else if (group.equalsIgnoreCase("Computer Science")) {

            subjects.add("Tamil");
            subjects.add("English");
            subjects.add("Physics");
            subjects.add("Chemistry");
            subjects.add("Mathematics");
            subjects.add("Computer Science");
        }

        // Commerce
        else if (group.equalsIgnoreCase("Commerce")) {

            subjects.add("Tamil");
            subjects.add("English");
            subjects.add("Accountancy");
            subjects.add("Commerce");
            subjects.add("Economics");
            subjects.add("Computer Applications");
        }

        // Humanities
        else if (group.equalsIgnoreCase("Humanities")) {

            subjects.add("Tamil");
            subjects.add("English");
            subjects.add("History");
            subjects.add("Economics");
            subjects.add("Political Science");
        }

        return subjects;
    }


    // UPDATE MARKS
    public static void updateMarks(Scanner sc) {

        System.out.println();
        System.out.println("UPDATE MARKS");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");
        sc.nextLine();

        try {

            Student student =
                    StudentOperations.getStudentById(studentId);

            if (student == null) {
                throw new StudentNotFoundException(
                        "Student Not Found..!!"
                );
            }

            HashMap<String, Integer> studentMarks =
                    marks.get(studentId);

            if (studentMarks == null ||
                    studentMarks.isEmpty()) {

                System.out.println(
                        "No Marks Found for this Student..!!"
                );

                return;
            }

            Staff currentStaff = LoginManager.getCurrentStaff();
            ArrayList<String> allowedSubjects = new ArrayList<>();

            if (currentStaff != null && currentStaff.getRole() == Role.SUBJECT_STAFF) {
                for (String subject : studentMarks.keySet()) {
                    for (String assignedSubject : currentStaff.getSubjects()) {
                        if (subject.equalsIgnoreCase(assignedSubject.trim())) {
                            allowedSubjects.add(subject);
                            break;
                        }
                    }
                }

                if (allowedSubjects.isEmpty()) {
                    System.out.println("Access Denied..!! You are not assigned to any valid subject for this student.");
                    return;
                }
            } else {
                allowedSubjects.addAll(studentMarks.keySet());
            }

            System.out.println();
            System.out.println("Existing Marks");
            System.out.println("------------------------------");

            for (String subject : allowedSubjects) {

                System.out.println(
                        subject + " : " +
                        studentMarks.get(subject)
                );
            }

            System.out.println("------------------------------");

            String subject;
            while (true) {
                System.out.print("Enter Subject : ");
                subject = sc.nextLine().trim();
                if (allowedSubjects.contains(subject)) {
                    break;
                }
                System.out.println("Subject Mark Not Found..!! Please enter one of the listed subjects.");
            }

            int mark = InputHelper.readMark(sc, "Enter " + subject + " Mark : ");

            if (mark < 0 || mark > 100) {

                throw new InvalidMarkException(
                        "Invalid Mark..!! Mark should be between 0 and 100."
                );
            }

            studentMarks.put(subject, mark);

            System.out.println();
            System.out.println("Mark Updated Successfully..!!");
            System.out.println("Student ID : " + studentId);
            System.out.println("Subject    : " + subject);
            System.out.println("New Mark   : " + mark);

        }
        catch (StudentNotFoundException e) {

            System.out.println(e.getMessage());

        }
        catch (InvalidMarkException e) {

            System.out.println(e.getMessage());
        }
    }


    // GET MARKS
    public static void getMarks(Scanner sc) {

        System.out.println();
        System.out.println("GET MARKS");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");

        for (String subject : studentMarks.keySet()) {

            System.out.println(
                    subject + " : " +
                    studentMarks.get(subject)
            );
        }

        System.out.println("------------------------------");
    }


    // TOTAL MARKS
    public static void totalMarks(Scanner sc) {

        System.out.println();
        System.out.println("TOTAL MARKS");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        int total = 0;

        for (int mark : studentMarks.values()) {
            total += mark;
        }

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");

        for (String subject : studentMarks.keySet()) {

            System.out.println(
                    subject + " : " +
                    studentMarks.get(subject)
            );
        }

        System.out.println("------------------------------");
        System.out.println("Total Marks  : " + total);
        System.out.println("Subjects     : " + studentMarks.size());
    }


    // AVERAGE MARKS
    public static void averageMarks(Scanner sc) {

        System.out.println();
        System.out.println("AVERAGE MARKS");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        int total = 0;

        for (int mark : studentMarks.values()) {
            total += mark;
        }

        double average =
                (double) total / studentMarks.size();

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");
        System.out.println("Total Marks  : " + total);
        System.out.println("Subjects     : " + studentMarks.size());

        System.out.printf(
                "Average Marks: %.2f%n",
                average
        );

        System.out.println("------------------------------");
    }


    // PERCENTAGE
    public static void percentage(Scanner sc) {

        System.out.println();
        System.out.println("PERCENTAGE");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        int total = 0;

        for (int mark : studentMarks.values()) {
            total += mark;
        }

        int totalSubjects = studentMarks.size();
        int maximumMarks = totalSubjects * 100;

        double percentage =
                ((double) total / maximumMarks) * 100;

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");
        System.out.println("Total Marks  : " + total);
        System.out.println("Maximum Marks: " + maximumMarks);

        System.out.printf(
                "Percentage   : %.2f%%%n",
                percentage
        );

        System.out.println("------------------------------");
    }


    // GRADE
    public static void grade(Scanner sc) {

        System.out.println();
        System.out.println("GRADE");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        int total = 0;

        for (int mark : studentMarks.values()) {
            total += mark;
        }

        double percentage =
                ((double) total /
                (studentMarks.size() * 100)) * 100;

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

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");

        System.out.printf(
                "Percentage   : %.2f%%%n",
                percentage
        );

        System.out.println("Grade        : " + grade);
        System.out.println("------------------------------");
    }


    // PASS / FAIL
    public static void passFail(Scanner sc) {

        System.out.println();
        System.out.println("PASS / FAIL");
        System.out.println("==============================");

        String studentId = InputHelper.readStudentId(sc, "Enter Student ID : ");

        Student student =
                StudentOperations.getStudentById(studentId);

        if (student == null) {

            System.out.println("Student Not Found..!!");
            return;
        }

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            System.out.println(
                    "No Marks Found for this Student..!!"
            );

            return;
        }

        boolean passed = true;

        for (int mark : studentMarks.values()) {

            if (mark < 35) {

                passed = false;
                break;
            }
        }

        System.out.println();
        System.out.println("Student Name : " + student.getName());
        System.out.println("Student ID   : " + student.getId());
        System.out.println("------------------------------");

        if (passed) {
            System.out.println("Result       : PASS");
        }
        else {
            System.out.println("Result       : FAIL");
        }

        System.out.println("------------------------------");
    }


    // HIGHEST MARKS
    public static void highestMarks(Scanner sc) {

        System.out.println();
        System.out.println("HIGHEST MARKS");
        System.out.println("==============================");

        System.out.print("Enter Subject : ");
        sc.nextLine();

        String subject = sc.nextLine();

        Student highestStudent = null;
        int highestMark = -1;

        for (Student student :
                StudentOperations.getStudents()) {

            Integer mark =
                    getMark(
                            student.getId(),
                            subject
                    );

            if (mark != null &&
                    mark > highestMark) {

                highestMark = mark;
                highestStudent = student;
            }
        }

        if (highestStudent == null) {

            System.out.println(
                    "No Marks Found for this Subject..!!"
            );

            return;
        }

        System.out.println();
        System.out.println("Highest Mark Student");
        System.out.println("------------------------------");

        System.out.println(
                "Student ID   : " +
                highestStudent.getId()
        );

        System.out.println(
                "Student Name : " +
                highestStudent.getName()
        );

        System.out.println(
                "Subject      : " + subject
        );

        System.out.println(
                "Highest Mark : " + highestMark
        );

        System.out.println("------------------------------");
    }


    // PASSED STUDENTS
    public static void passedStudents() {

        System.out.println();
        System.out.println("PASSED STUDENTS");
        System.out.println("=======================");

        boolean found = false;

        for (Student student :
                StudentOperations.getStudents()) {

            HashMap<String, Integer> studentMarks =
                    marks.get(student.getId());

            if (studentMarks == null ||
                    studentMarks.isEmpty()) {

                continue;
            }

            boolean passed = true;

            for (int mark : studentMarks.values()) {

                if (mark < 35) {

                    passed = false;
                    break;
                }
            }

            if (passed) {

                found = true;

                System.out.println(
                        "ID: " + student.getId()
                        + " | Name: " + student.getName()
                );
            }
        }

        if (!found) {

            System.out.println(
                    "No Passed Students Found..!!"
            );
        }

        System.out.println("------------------------------");
    }


    // STUDENTS ABOVE 50%
    public static void studentsAbove50() {

        System.out.println();
        System.out.println("STUDENTS ABOVE 50%");
        System.out.println("==============================");

        boolean found = false;

        for (Student student :
                StudentOperations.getStudents()) {

            HashMap<String, Integer> studentMarks =
                    marks.get(student.getId());

            if (studentMarks == null ||
                    studentMarks.isEmpty()) {

                continue;
            }

            int total = 0;

            for (int mark : studentMarks.values()) {
                total += mark;
            }

            double percentage =
                    ((double) total /
                    (studentMarks.size() * 100)) * 100;

            if (percentage > 50) {

                found = true;

                System.out.printf(
                        "ID: %d | Name: %s | Percentage: %.2f%%%n",
                        student.getId(),
                        student.getName(),
                        percentage
                );
            }
        }

        if (!found) {

            System.out.println(
                    "No Students Found Above 50%..!!"
            );
        }

        System.out.println("------------------------------");
    }


    // SORT MARKS DESCENDING
    public static void sortMarks() {

        System.out.println();
        System.out.println("SORT STUDENTS BY MARKS");
        System.out.println("==============================");

        ArrayList<Student> students =
                new ArrayList<>(
                        StudentOperations.getStudents()
                );

        if (students.isEmpty()) {

            System.out.println("No Students Found..!!");
            return;
        }

        students.sort(new Comparator<Student>() {

            @Override
            public int compare(
                    Student s1,
                    Student s2) {

                int total1 =
                        getTotalMarks(
                                s1.getId()
                        );

                int total2 =
                        getTotalMarks(
                                s2.getId()
                        );

                return Integer.compare(
                        total2,
                        total1
                );
            }
        });

        for (Student student : students) {

            int total =
                    getTotalMarks(
                            student.getId()
                    );

            System.out.println(
                    "ID: " + student.getId()
                    + " | Name: " + student.getName()
                    + " | Total Marks: " + total
            );
        }

        System.out.println("------------------------------");
    }


    // =========================
    // GET TOTAL MARKS
    // =========================

    private static int getTotalMarks(
            String studentId) {

        HashMap<String, Integer> studentMarks =
                marks.get(studentId);

        if (studentMarks == null ||
                studentMarks.isEmpty()) {

            return 0;
        }

        int total = 0;

        for (int mark : studentMarks.values()) {
            total += mark;
        }

        return total;
    }


    // =========================
    // GET MARK
    // =========================

    public static Integer getMark(
            String studentId,
            String subject) {

        if (marks.containsKey(studentId)) {

            return marks.get(studentId).get(subject);
        }

        return null;
    }


    // =========================
    // GET STUDENT MARKS
    // =========================

    public static HashMap<String, Integer>
            getStudentMarks(String studentId) {

        return marks.get(studentId);
    }


    // =========================
    // GET ALL MARKS
    // =========================

    public static HashMap<String, HashMap<String, Integer>>
            getMarks() {

        return marks;
    }


    // =========================
    // REMOVE STUDENT MARKS
    // =========================

    public static void removeMarks(
            String studentId) {

        marks.remove(studentId);
    }
}