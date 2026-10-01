package com.littlebirds.sms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.littlebirds.sms.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, String> {

    /** Optional filter by subject name (case-insensitive). Pass null to list all teachers. */
    @Query("""
            select distinct t from Teacher t
            left join t.subjects sub
            where (:subject is null or lower(sub) = lower(:subject))
            """)
    Page<Teacher> search(@Param("subject") String subject, Pageable pageable);

    /** Console feature "Teachers More Than 2 Subjects". */
    @Query("select t from Teacher t where size(t.subjects) > 2")
    List<Teacher> findWithMoreThanTwoSubjects();
}
