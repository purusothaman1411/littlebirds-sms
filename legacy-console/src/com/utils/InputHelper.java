package com.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHelper {

    public static int readInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter numbers only.");
                sc.nextLine();
            }
        }
    }

    public static int readPositiveInt(Scanner sc, String message) {
        while (true) {
            int value = readInt(sc, message);
            if (value > 0) return value;
            System.out.println("Value must be greater than 0. Please try again.");
        }
    }

    public static int readAge(Scanner sc, String message) {
        while (true) {
            int age = readInt(sc, message);
            if (age >= 3 && age <= 100) return age;
            System.out.println("Invalid age. Please enter an age between 3 and 100.");
        }
    }

    public static String readRequiredString(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    public static String readName(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else if (!value.matches("[a-zA-Z ]+")) {
                System.out.println("Name should contain letters and spaces only.");
            } else {
                return value;
            }
        }
    }

    public static String readRequiredToken(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.next().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    public static String readStudentId(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.next().trim();
            if (value.matches("[A-Za-z][A-Za-z0-9_-]{0,19}")) return value;
            System.out.println("Invalid Student ID. Use letters/numbers, e.g. S101 or ST101.");
        }
    }

    public static String readDate(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.matches("\\d{2}-\\d{2}-\\d{4}")) {
                try {
                    String[] parts = value.split("-");
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.of(year, month, day);
                    if (!date.isAfter(LocalDate.now())) return value;
                } catch (DateTimeException | NumberFormatException e) {
                    // Invalid calendar date or malformed input.
                }
            }
            System.out.println("Invalid date. Use DD-MM-YYYY format.");
        }
    }

    public static String readGender(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.equalsIgnoreCase("Male") || value.equalsIgnoreCase("Female") || value.equalsIgnoreCase("Other")) {
                return value;
            }
            System.out.println("Invalid gender. Enter Male, Female, or Other.");
        }
    }

    public static String readStandard(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim().toLowerCase();
            if (value.matches("[1-9]|10")) return value;
            if (value.equals("11") || value.equals("11th")) return "11";
            if (value.equals("12") || value.equals("12th")) return "12";
            System.out.println("Invalid standard. Enter a standard from 1 to 12.");
        }
    }

    public static String readEmail(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Email cannot be empty.");
            } else if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println("Invalid Email ID. Please enter a valid email.");
            } else {
                return value;
            }
        }
    }

    public static String readMobile(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.matches("\\+91\\s?[6-9]\\d{9}")) return value;
            System.out.println("Invalid Mobile Number! Enter a valid mobile number with +91 country code.");
        }
    }

    public static int readMark(Scanner sc, String message) {
        while (true) {
            int mark = readInt(sc, message);
            if (mark >= 0 && mark <= 100) return mark;
            System.out.println("Invalid Mark! Enter a mark between 0 and 100.");
        }
    }


    public static int readChoice(Scanner sc, String message, int min, int max) {
        while (true) {
            int value = readInt(sc, message);
            if (value >= min && value <= max) return value;
            System.out.println("Invalid choice. Please enter a value between " + min + " and " + max + ".");
        }
    }

    public static String readAttendanceStatus(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String value = sc.next().trim();
            if (value.equalsIgnoreCase("P") || value.equalsIgnoreCase("A")) return value.toUpperCase();
            System.out.println("Invalid Attendance! Enter P for Present or A for Absent.");
        }
    }
}
