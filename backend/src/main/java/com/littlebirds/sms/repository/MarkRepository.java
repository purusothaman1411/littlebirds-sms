package com.littlebirds.sms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.littlebirds.sms.entity.Mark;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findByStudent_StudentId(String studentId);

    Optional<Mark> findByStudent_StudentIdAndSubject_Id(String studentId, Long subjectId);

    List<Mark> findByStudent_Standard(Integer standard);

    /** Highest mark in a subject (ties: lowest student ID first, deterministic). */
    Optional<Mark> findFirstBySubject_NameIgnoreCaseOrderByMarkDescStudent_StudentIdAsc(String subjectName);

    /** All marks with student and subject loaded, for school-wide rankings and results (avoids N+1 queries). */
    @Query("select m from Mark m join fetch m.student join fetch m.subject")
    List<Mark> findAllWithStudent();
}
