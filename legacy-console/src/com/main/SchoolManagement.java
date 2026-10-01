package com.main;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.operations.StudentOperations;
import com.operations.TeacherOperations;
import com.operations.MarksOperations;
import com.operations.AttendanceOperations;
import com.operations.ReportOperations;
import com.staff.Role;
import com.staff.StaffPermissions;
import com.staff.Staff;
import com.authentication.LoginManager;

public class SchoolManagement {

    private static Role currentRole;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Staff loggedInStaff = LoginManager.login(sc);

        if (loggedInStaff == null) {
            System.out.println("Access Denied..!");
            sc.close();
            return;
        }

        currentRole = loggedInStaff.getRole();

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("       SCHOOL MANAGEMENT SYSTEM");
                System.out.println("======================================");

                // STUDENT MENU
                if (StaffPermissions.canAccessStudentMenu(currentRole)) {
                    System.out.println(menuNumber + ". Student Management");
                    menuNumber++;
                }

                // TEACHER MENU
                if (StaffPermissions.canAccessTeacherMenu(currentRole)) {
                    System.out.println(menuNumber + ". Teacher Management");
                    menuNumber++;
                }

                // MARKS MENU
                if (StaffPermissions.canAccessMarksMenu(currentRole)) {
                    System.out.println(menuNumber + ". Marks Management");
                    menuNumber++;
                }

                // ATTENDANCE MENU
                if (StaffPermissions.canAccessAttendanceMenu(currentRole)) {
                    System.out.println(menuNumber + ". Attendance Management");
                    menuNumber++;
                }

                // REPORT MENU
                if (StaffPermissions.canAccessReportMenu(currentRole)) {
                    System.out.println(menuNumber + ". Report Card");
                    menuNumber++;
                }

                // EXIT
                System.out.println(menuNumber + ". Exit");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                // -------------------------------------------------
                // DYNAMIC MAIN MENU HANDLING
                // -------------------------------------------------

                int selectedMenu = 1;

                // STUDENT
                if (StaffPermissions.canAccessStudentMenu(currentRole)) {

                    if (option == selectedMenu) {
                        studentMenu(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // TEACHER
                if (StaffPermissions.canAccessTeacherMenu(currentRole)) {

                    if (option == selectedMenu) {
                        teacherMenu(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // MARKS
                if (StaffPermissions.canAccessMarksMenu(currentRole)) {

                    if (option == selectedMenu) {
                        marksMenu(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // ATTENDANCE
                if (StaffPermissions.canAccessAttendanceMenu(currentRole)) {

                    if (option == selectedMenu) {
                        attendanceMenu(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // REPORT
                if (StaffPermissions.canAccessReportMenu(currentRole)) {

                    if (option == selectedMenu) {
                        reportMenu(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // EXIT
                if (option == selectedMenu) {

                    System.out.println();
                    System.out.println(
                            "Thank You for Using School Management System..!!"
                    );
                    System.out.println(
                            "---------------------------------------------------------------"
                    );

                    sc.close();
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }


    // ======================================
    // STUDENT MENU
    // ======================================

    public static void studentMenu(Scanner sc) {

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("        STUDENT MANAGEMENT");
                System.out.println("======================================");

                // ADD STUDENT
                if (StaffPermissions.canAddStudent(currentRole)) {
                    System.out.println(menuNumber + ". Add Student");
                    menuNumber++;
                }

                // VIEW STUDENT
                if (StaffPermissions.canViewStudent(currentRole)) {
                    System.out.println(menuNumber + ". Get Student");
                    menuNumber++;

                    System.out.println(menuNumber + ". Get All Students");
                    menuNumber++;

                    System.out.println(menuNumber + ". Search Student");
                    menuNumber++;
                }

                // UPDATE STUDENT
                if (StaffPermissions.canUpdateStudent(currentRole)) {
                    System.out.println(menuNumber + ". Update Student");
                    menuNumber++;
                }

                // DELETE STUDENT
                if (StaffPermissions.canDeleteStudent(currentRole)) {
                    System.out.println(menuNumber + ". Delete Student");
                    menuNumber++;
                }

                System.out.println(menuNumber + ". Back");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                int selectedMenu = 1;

                // ADD
                if (StaffPermissions.canAddStudent(currentRole)) {

                    if (option == selectedMenu) {
                        StudentOperations.addStudent(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // VIEW
                if (StaffPermissions.canViewStudent(currentRole)) {

                    if (option == selectedMenu) {
                        StudentOperations.getStudent(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        StudentOperations.getAllStudents();
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        StudentOperations.searchStudent(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // UPDATE
                if (StaffPermissions.canUpdateStudent(currentRole)) {

                    if (option == selectedMenu) {
                        StudentOperations.updateStudent(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // DELETE
                if (StaffPermissions.canDeleteStudent(currentRole)) {

                    if (option == selectedMenu) {
                        StudentOperations.deleteStudent(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // BACK
                if (option == selectedMenu) {
                    System.out.println("Back To Main Menu...");
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }


    // ======================================
    // TEACHER MENU
    // ======================================

    public static void teacherMenu(Scanner sc) {

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("         STAFF MANAGEMENT");
                System.out.println("======================================");

                // ADD TEACHER
                if (StaffPermissions.canAddTeacher(currentRole)) {
                    System.out.println(menuNumber + ". Add Teacher");
                    menuNumber++;
                }

                // VIEW TEACHER
                if (StaffPermissions.canViewTeacher(currentRole)) {

                    System.out.println(menuNumber + ". Get Teacher");
                    menuNumber++;

                    System.out.println(menuNumber + ". Get All Teachers");
                    menuNumber++;

                    System.out.println(menuNumber + ". Search By Subject");
                    menuNumber++;

                    System.out.println(
                            menuNumber + ". Teachers More Than 2 Subjects"
                    );
                    menuNumber++;
                }

                // UPDATE TEACHER
                if (StaffPermissions.canUpdateTeacher(currentRole)) {
                    System.out.println(menuNumber + ". Update Teacher");
                    menuNumber++;
                }

                // DELETE TEACHER
                if (StaffPermissions.canDeleteTeacher(currentRole)) {
                    System.out.println(menuNumber + ". Delete Teacher");
                    menuNumber++;
                }

                System.out.println(menuNumber + ". Back");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                int selectedMenu = 1;

                // ADD
                if (StaffPermissions.canAddTeacher(currentRole)) {

                    if (option == selectedMenu) {
                        TeacherOperations.addTeacher(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // VIEW
                if (StaffPermissions.canViewTeacher(currentRole)) {

                    if (option == selectedMenu) {
                        TeacherOperations.getTeacher(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        TeacherOperations.getAllTeachers();
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        TeacherOperations.searchTeacherBySubject(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        TeacherOperations.teachersMoreThanTwoSubjects();
                        continue;
                    }

                    selectedMenu++;
                }

                // UPDATE
                if (StaffPermissions.canUpdateTeacher(currentRole)) {

                    if (option == selectedMenu) {
                        TeacherOperations.updateTeacher(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // DELETE
                if (StaffPermissions.canDeleteTeacher(currentRole)) {

                    if (option == selectedMenu) {
                        TeacherOperations.deleteTeacher(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // BACK
                if (option == selectedMenu) {
                    System.out.println("Back To Main Menu...");
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }


    // ======================================
    // MARKS MENU
    // ======================================

    public static void marksMenu(Scanner sc) {

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("          MARKS MANAGEMENT");
                System.out.println("======================================");

                // ADD MARKS
                if (StaffPermissions.canAddMarks(currentRole)) {
                    System.out.println(menuNumber + ". Add Marks");
                    menuNumber++;
                }

                // UPDATE MARKS
                if (StaffPermissions.canUpdateMarks(currentRole)) {
                    System.out.println(menuNumber + ". Update Marks");
                    menuNumber++;
                }

                // VIEW MARKS
                if (StaffPermissions.canViewMarks(currentRole)) {

                    System.out.println(menuNumber + ". View Marks");
                    menuNumber++;

                    System.out.println(menuNumber + ". Total Marks");
                    menuNumber++;

                    System.out.println(menuNumber + ". Average");
                    menuNumber++;

                    System.out.println(menuNumber + ". Percentage");
                    menuNumber++;

                    System.out.println(menuNumber + ". Grade");
                    menuNumber++;

                    System.out.println(menuNumber + ". Pass / Fail");
                    menuNumber++;

                    System.out.println(
                            menuNumber + ". Highest Score Each Subject"
                    );
                    menuNumber++;

                    System.out.println(
                            menuNumber + ". Students Who Passed"
                    );
                    menuNumber++;

                    System.out.println(
                            menuNumber + ". Students Above 50"
                    );
                    menuNumber++;

                    System.out.println(
                            menuNumber + ". Sort Marks Descending"
                    );
                    menuNumber++;
                }

                System.out.println(menuNumber + ". Back");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                int selectedMenu = 1;

                // ADD
                if (StaffPermissions.canAddMarks(currentRole)) {

                    if (option == selectedMenu) {
                        MarksOperations.addMarks(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // UPDATE
                if (StaffPermissions.canUpdateMarks(currentRole)) {

                    if (option == selectedMenu) {
                        MarksOperations.updateMarks(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // VIEW
                if (StaffPermissions.canViewMarks(currentRole)) {

                    if (option == selectedMenu) {
                        MarksOperations.getMarks(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.totalMarks(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.averageMarks(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.percentage(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.grade(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.passFail(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.highestMarks(sc);
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.passedStudents();
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.studentsAbove50();
                        continue;
                    }

                    selectedMenu++;

                    if (option == selectedMenu) {
                        MarksOperations.sortMarks();
                        continue;
                    }

                    selectedMenu++;
                }

                // BACK
                if (option == selectedMenu) {
                    System.out.println("Back To Main Menu...");
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }


    // ======================================
    // ATTENDANCE MENU
    // ======================================

    public static void attendanceMenu(Scanner sc) {

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("       ATTENDANCE MANAGEMENT");
                System.out.println("======================================");

                // MARK ATTENDANCE
                if (StaffPermissions.canMarkAttendance(currentRole)) {
                    System.out.println(menuNumber + ". Mark Attendance");
                    menuNumber++;
                }

                // VIEW ATTENDANCE
                if (StaffPermissions.canViewAttendance(currentRole)) {
                    System.out.println(menuNumber + ". View Attendance");
                    menuNumber++;
                }

                System.out.println(menuNumber + ". Back");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                int selectedMenu = 1;

                // MARK
                if (StaffPermissions.canMarkAttendance(currentRole)) {

                    if (option == selectedMenu) {
                        AttendanceOperations.markAttendance(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // VIEW
                if (StaffPermissions.canViewAttendance(currentRole)) {

                    if (option == selectedMenu) {
                        AttendanceOperations.getAttendance(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // BACK
                if (option == selectedMenu) {
                    System.out.println("Back To Main Menu...");
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }


    // ======================================
    // REPORT MENU
    // ======================================

    public static void reportMenu(Scanner sc) {

        while (true) {

            try {

                int menuNumber = 1;

                System.out.println();
                System.out.println("======================================");
                System.out.println("       REPORT CARD MANAGEMENT");
                System.out.println("======================================");

                // REPORT CARD
                if (StaffPermissions.canViewReportCard(currentRole)) {
                    System.out.println(
                            menuNumber + ". Generate Report Card"
                    );
                    menuNumber++;
                }

                // CLASS WISE RESULTS
                if (StaffPermissions.canViewReports(currentRole)) {
                    System.out.println(
                            menuNumber + ". Class Wise Results"
                    );
                    menuNumber++;
                }

                System.out.println(menuNumber + ". Back");

                System.out.println("======================================");

                System.out.print("Choose your option : ");
                int option = sc.nextInt();

                int selectedMenu = 1;

                // REPORT CARD
                if (StaffPermissions.canViewReportCard(currentRole)) {

                    if (option == selectedMenu) {
                        ReportOperations.generateReportCard(sc);
                        continue;
                    }

                    selectedMenu++;
                }

                // CLASS WISE RESULTS
                if (StaffPermissions.canViewReports(currentRole)) {

                    if (option == selectedMenu) {
                        ReportOperations.classWiseResults();
                        continue;
                    }

                    selectedMenu++;
                }

                // BACK
                if (option == selectedMenu) {
                    System.out.println("Back To Main Menu...");
                    return;
                }

                System.out.println("Invalid Option..!!");

            } catch (InputMismatchException e) {

                System.out.println("Please Enter Numbers Only..!!");
                sc.nextLine();
            }
        }
    }
}