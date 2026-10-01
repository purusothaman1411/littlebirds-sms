package com.authentication;

import java.util.ArrayList;
import java.util.Scanner;

import com.filehandling.StaffFileHandler;
import com.staff.Staff;
import com.utils.InputHelper;

public class LoginManager {

    private static Staff currentStaff;

    public static Staff getCurrentStaff() {
        return currentStaff;
    }

    public static Staff login(Scanner sc) {

        ArrayList<Staff> staffList = StaffFileHandler.loadStaff();

        System.out.println();
        System.out.println("======================================");
        System.out.println("               LOGIN");
        System.out.println("======================================");

        String username = InputHelper.readRequiredToken(sc, "Enter Username : ");

        String password = InputHelper.readRequiredToken(sc, "Enter Password : ");

        for (Staff staff : staffList) {

            if (staff.getUsername().equals(username)
                    && staff.getPassword().equals(password)) {

                currentStaff = staff;

                System.out.println();
                System.out.println("Login Successful..!!");
                System.out.println("Welcome " + staff.getName() + "..!!");
                System.out.println("Role : " + staff.getRole());

                return staff;
            }
        }

        System.out.println();
        System.out.println("Invalid Username or Password..!!");

        return null;
    }
}