package com.classes;

import java.util.ArrayList;

public class Teacher {

    private String id;
    private String name;
    private String dob;
    private String gender;
    private String qualification;
    private String email;
    private String contact;
    private String address;
    private ArrayList<String> subjects;


    // Constructor
    public Teacher(
            String id,
            String name,
            String dob,
            String gender,
            String qualification,
            String email,
	       String contact,
	       String address,
            ArrayList<String> subjects) {

        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.qualification = qualification;
        this.email = email;
	this.contact = contact;
	this.address = address;
        this.subjects = subjects;
    }


    // Get ID
    public String getId() {
        return id;
    }


    // Set ID
    public void setId(String id) {
        this.id = id;
    }


    // Get Name
    public String getName() {
        return name;
    }


    // Set Name
    public void setName(String name) {
        this.name = name;
    }

    public String getDob(){
        return dob;
    }

    public void setDob(String dob){
        this.dob = dob;
    }

    // Get Gender
    public String getGender() {
        return gender;
    }


    // Set Gender
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getQualification(){
        return qualification;
    } 

    public void setQualification(String qualification){
        this.qualification = qualification;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }


     // Get Contact

	public String getContact(){
		return contact;
	}

     // Set Contact
	public void setContact(String contact){
		this.contact = contact;
	}

    // Get Address

	public String getAddress(){
		return address;
	}

      // Set Address
	public void setAddress(String address){
		this.address = address;
	}




    // Get Subjects
    public ArrayList<String> getSubjects() {
        return subjects;
    }


    // Set Subjects
    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }


    // Add Subject
    public void addSubject(String subject) {

        if (subjects == null) {
            subjects = new ArrayList<>();
        }

        subjects.add(subject);
    }


    // Display Teacher
    @Override
    public String toString() {

        return "Teacher ID      : " + id
                + "\nTeacher Name    : " + name
                + "\nDob             : " + dob
                + "\nGender          : " + gender
                + "\nQualification   : " + qualification
                + "\nEmail           : " + email
                + "\nContact         : " + contact
                + "\nAddress         : " + address
                + "\nSubjects        : " + subjects;
    }
}