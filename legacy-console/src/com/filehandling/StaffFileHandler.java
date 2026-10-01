package com.filehandling;

import com.staff.Role;
import com.staff.Staff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StaffFileHandler {

    private static final String FILE_PATH = "data/staff.txt";

    public static Staff getStaffById(ArrayList<Staff> staffList, String staffId) {
        if (staffList == null || staffId == null) {
            return null;
        }

        for (Staff staff : staffList) {
            if (staff != null && staff.getStaffId().equalsIgnoreCase(staffId)) {
                return staff;
            }
        }

        return null;
    }

    public static void addStaff(ArrayList<Staff> staffList, Staff staff) {
        if (staffList == null || staff == null) {
            return;
        }

        if (getStaffById(staffList, staff.getStaffId()) == null) {
            staffList.add(staff);
        }
    }

    public static void updateStaff(ArrayList<Staff> staffList, Staff updatedStaff) {
        if (staffList == null || updatedStaff == null) {
            return;
        }

        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equalsIgnoreCase(updatedStaff.getStaffId())) {
                staffList.set(i, updatedStaff);
                return;
            }
        }
    }

    public static void deleteStaff(ArrayList<Staff> staffList, String staffId) {
        if (staffList == null || staffId == null) {
            return;
        }

        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equalsIgnoreCase(staffId)) {
                staffList.remove(i);
                return;
            }
        }
    }

    private static String formatSubjects(ArrayList<String> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return "ALL";
        }

        if (subjects.size() == 1 && "ALL".equalsIgnoreCase(subjects.get(0))) {
            return "ALL";
        }

        return String.join(",", subjects);
    }

    public static void saveStaffs(ArrayList<Staff> staffList) {
        File file = new File(FILE_PATH);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            if (staffList == null) {
                return;
            }

            for (Staff staff : staffList) {
                if (staff == null) {
                    continue;
                }

                String data = staff.getStaffId() + "|" +
                        staff.getName() + "|" +
                        staff.getUsername() + "|" +
                        staff.getPassword() + "|" +
                        staff.getRole() + "|" +
                        staff.getClassRange() + "|" +
                        staff.getGroup() + "|" +
                        formatSubjects(staff.getSubjects());

                bw.write(data);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving staff data: " + e.getMessage());
        }
    }

    public static void saveStaff(ArrayList<Staff> staffList) {
        saveStaffs(staffList);
    }

    public static ArrayList<Staff> loadStaff() {

        ArrayList<Staff> staffList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data.length < 8) {
                    continue;
                }

                String staffId = data[0];
                String name = data[1];
                String username = data[2];
                String password = data[3];

                Role role = Role.valueOf(data[4]);

                String classRange = data[5];
                String group = data[6];

                ArrayList<String> subjects = new ArrayList<>();

                if (!data[7].equalsIgnoreCase("ALL")) {

                    String[] subjectList = data[7].split(",");

                    for (String subject : subjectList) {
                        subjects.add(subject.trim());
                    }

                } else {
                    subjects.add("ALL");
                }

                Staff staff = new Staff(
                        staffId,
                        name,
                        username,
                        password,
                        role,
                        classRange,	
                        group,
                        subjects
                );

                staffList.add(staff);
            }

        } catch (IOException e) {

            System.out.println("Error reading staff data: " + e.getMessage());
        }

        return staffList;
    }
}