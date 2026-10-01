package com.classes;

public class Student {

    private String id;
    private String name;
    private String dob;
    private int age;
    private String gender;
    private String standard;
    private String address;
    private String contactNumber;
    private String group;

    public Student(
            String id,
            String name,
            String dob,
            int age,
            String gender,
            String standard,
            String address,
            String contactNumber,
            String group) {

        this.id = id;
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.standard = standard;
        this.address = address;
        this.contactNumber = contactNumber;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob(){
        return dob;
    }

    public void setDob(String dob){
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {

        return "Student ID      : " + id
                + "\nStudent Name    : " + name
                + "\nDob             : " + dob
                + "\nAge             : " + age
                + "\nGender          : " + gender
                + "\nStandard        : " + standard
                + "\nAddress         : " + address
                + "\nContact Number  : " + contactNumber
                + "\nGroup           : " + group;
    }
}