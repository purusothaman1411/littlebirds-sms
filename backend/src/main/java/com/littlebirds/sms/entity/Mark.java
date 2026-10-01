package com.littlebirds.sms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** One mark for one student in one subject (unique per student+subject). */
@Entity
@Table(name = "marks")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Integer mark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entered_by")
    private StaffUser enteredBy;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Mark() {
    }

    public Mark(Student student, Subject subject, Integer mark, StaffUser enteredBy) {
        this.student = student;
        this.subject = subject;
        this.mark = mark;
        this.enteredBy = enteredBy;
    }

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Subject getSubject() { return subject; }
    public Integer getMark() { return mark; }
    public void setMark(Integer mark) { this.mark = mark; }
    public StaffUser getEnteredBy() { return enteredBy; }
    public void setEnteredBy(StaffUser enteredBy) { this.enteredBy = enteredBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
