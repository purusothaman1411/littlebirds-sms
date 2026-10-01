package com.littlebirds.sms.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** One attendance entry per student per date. "No future dates" is enforced in the service. */
@Entity
@Table(name = "attendance")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false, length = 1)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private StaffUser markedBy;

    protected AttendanceRecord() {
    }

    public AttendanceRecord(Student student, LocalDate attendanceDate, AttendanceStatus status, StaffUser markedBy) {
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.markedBy = markedBy;
    }

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public StaffUser getMarkedBy() { return markedBy; }
    public void setMarkedBy(StaffUser markedBy) { this.markedBy = markedBy; }
}
