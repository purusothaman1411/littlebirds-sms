package com.littlebirds.sms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.littlebirds.sms.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {

    /** Optional filters: partial name match and/or exact standard. Pass null to skip a filter. */
    @Query("""
            select s from Student s
            where (:standard is null or s.standard = :standard)
              and (:name is null or lower(s.name) like lower(concat('%', :name, '%')))
            """)
    Page<Student> search(@Param("name") String name, @Param("standard") Integer standard, Pageable pageable);

    List<Student> findByStandardOrderByStudentId(Integer standard);

    List<Student> findAllByOrderByStandardAscStudentIdAsc();
}
