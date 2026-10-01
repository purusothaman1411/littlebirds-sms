package com.littlebirds.sms.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.littlebirds.sms.dto.OnCreate;
import java.net.URI;

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
import com.littlebirds.sms.dto.StudentRequest;
import com.littlebirds.sms.dto.StudentResponse;
import com.littlebirds.sms.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Console "Get All Students" + "Search Student" (partial name) + filter by standard, paged. */
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @GetMapping
    public PageResponse<StudentResponse> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "standard", required = false) Integer standard,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return studentService.search(name, standard, page, size);
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable("id") String id) {
        return studentService.get(id);
    }

    @PreAuthorize("hasAuthority('STUDENT_ADD')")
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Validated(OnCreate.class) @RequestBody StudentRequest request) {
        StudentResponse created = studentService.create(request);
        return ResponseEntity.created(URI.create("/api/students/" + created.studentId())).body(created);
    }

    @PreAuthorize("hasAuthority('STUDENT_UPDATE')")
    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable("id") String id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @PreAuthorize("hasAuthority('STUDENT_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
