package com.littlebirds.sms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/** Teacher profile record (separate from StaffUser login accounts). No age field, as in the console app. */
@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @Column(name = "teacher_id", length = 20)
    private String teacherId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false, length = 100)
    private String qualification;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "contact_number", nullable = false, length = 10)
    private String contactNumber;

    @Column(nullable = false)
    private String address;

    /** Free-text subject names, as in the console app. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "subject_name", nullable = false, length = 50)
    private Set<String> subjects = new LinkedHashSet<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Teacher() {
    }

    public Teacher(String teacherId, String name, LocalDate dob, Gender gender, String qualification,
                   String email, String contactNumber, String address, Set<String> subjects) {
        this.teacherId = teacherId;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.qualification = qualification;
        this.email = email;
        this.contactNumber = contactNumber;
        this.address = address;
        this.subjects = subjects;
    }

    public String getTeacherId() { return teacherId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Set<String> getSubjects() { return subjects; }
    public void setSubjects(Set<String> subjects) { this.subjects = subjects; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
