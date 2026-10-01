package com.littlebirds.sms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Age is not stored: it is computed from the date of birth.
 * groupName is null for standards 1-10 and required for 11-12 (enforced by the service and a DB CHECK).
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false)
    private Integer standard;

    @Column(name = "group_name", length = 30)
    private String groupName;

    @Column(nullable = false)
    private String address;

    @Column(name = "contact_number", nullable = false, length = 10)
    private String contactNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Student() {
    }

    public Student(String studentId, String name, LocalDate dob, Gender gender, Integer standard,
                   String groupName, String address, String contactNumber) {
        this.studentId = studentId;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.standard = standard;
        this.groupName = groupName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    /** Computed, never persisted (field access is used, so this method is not a mapped property). */
    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public Integer getStandard() { return standard; }
    public void setStandard(Integer standard) { this.standard = standard; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
