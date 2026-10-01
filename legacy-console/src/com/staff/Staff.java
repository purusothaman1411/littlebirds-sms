package com.staff;

import java.util.ArrayList;

public class Staff {

    private String staffId;
    private String name;
    private String username;
    private String password;
    private Role role;

    private String classRange;
    private String group;
    private ArrayList<String> subjects;

    public Staff(String staffId, String name, String username,
                 String password, Role role,
                 String classRange, String group,
                 ArrayList<String> subjects) {

        this.staffId = staffId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.classRange = classRange;
        this.group = group;
        this.subjects = subjects;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getClassRange() {
        return classRange;
    }

    public String getGroup() {
        return group;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setClassRange(String classRange) {
        this.classRange = classRange;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }
}