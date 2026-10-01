package com.littlebirds.sms.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.littlebirds.sms.dto.OnCreate;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.PageResponse;
import com.littlebirds.sms.dto.TeacherRequest;
import com.littlebirds.sms.dto.TeacherResponse;
import com.littlebirds.sms.service.TeacherService;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /** Console "Get All Teachers" and "Search By Subject" (subject filter), paged. */
    @PreAuthorize("hasAuthority('TEACHER_VIEW')")
    @GetMapping
    public PageResponse<TeacherResponse> search(
            @RequestParam(name = "subject", required = false) String subject,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return teacherService.search(subject, page, size);
    }

    /** Console "Teachers More Than 2 Subjects". */
    @PreAuthorize("hasAuthority('TEACHER_VIEW')")
    @GetMapping("/more-than-two-subjects")
    public List<TeacherResponse> moreThanTwoSubjects() {
        return teacherService.withMoreThanTwoSubjects();
    }

    @PreAuthorize("hasAuthority('TEACHER_VIEW')")
    @GetMapping("/{id}")
    public TeacherResponse get(@PathVariable("id") String id) {
        return teacherService.get(id);
    }

    @PreAuthorize("hasAuthority('TEACHER_ADD')")
    @PostMapping
    public ResponseEntity<TeacherResponse> create(@Validated(OnCreate.class) @RequestBody TeacherRequest request) {
        TeacherResponse created = teacherService.create(request);
        return ResponseEntity.created(URI.create("/api/teachers/" + created.teacherId())).body(created);
    }

    @PreAuthorize("hasAuthority('TEACHER_UPDATE')")
    @PutMapping("/{id}")
    public TeacherResponse update(@PathVariable("id") String id, @Valid @RequestBody TeacherRequest request) {
        return teacherService.update(id, request);
    }

    @PreAuthorize("hasAuthority('TEACHER_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
