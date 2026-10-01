package com.littlebirds.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One subject in the curriculum of a level/group.
 * groupName is "NONE" for standards 1-10 (see schema.sql).
 */
@Entity
@Table(name = "curriculum")
public class CurriculumEntry {

    public static final String NO_GROUP = "NONE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CurriculumLevel level;

    @Column(name = "group_name", nullable = false, length = 30)
    private String groupName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    protected CurriculumEntry() {
    }

    public Long getId() { return id; }
    public CurriculumLevel getLevel() { return level; }
    public String getGroupName() { return groupName; }
    public Subject getSubject() { return subject; }
}
