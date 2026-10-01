package com.littlebirds.sms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.littlebirds.sms.entity.CurriculumEntry;
import com.littlebirds.sms.entity.CurriculumLevel;
import com.littlebirds.sms.entity.Subject;

public interface CurriculumRepository extends JpaRepository<CurriculumEntry, Long> {

    /** Subjects a student takes, for a level and group ("NONE" for standards 1-10). */
    @Query("""
            select c.subject from CurriculumEntry c
            where c.level = :level and c.groupName = :groupName
            order by c.id
            """)
    List<Subject> findSubjects(@Param("level") CurriculumLevel level, @Param("groupName") String groupName);
}
