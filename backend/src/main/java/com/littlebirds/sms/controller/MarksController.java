package com.littlebirds.sms.controller;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.HighestMarkResponse;
import com.littlebirds.sms.dto.MarksRequest;
import com.littlebirds.sms.dto.StudentMarksResponse;
import com.littlebirds.sms.dto.StudentResultRow;
import com.littlebirds.sms.security.StaffPrincipal;
import com.littlebirds.sms.service.MarksService;

/**
 * Marks. The console's Total, Average, Percentage, Grade and Pass/Fail options are all part of the
 * summary returned by GET /api/students/{id}/marks.
 */
@RestController
@RequestMapping("/api")
public class MarksController {

    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @PreAuthorize("hasAuthority('MARKS_VIEW')")
    @GetMapping("/students/{id}/marks")
    public StudentMarksResponse getMarks(@PathVariable("id") String id) {
        return marksService.getMarks(id);
    }

    /** Adds or updates the subjects sent in the body. */
    @PreAuthorize("hasAnyAuthority('MARKS_ADD', 'MARKS_UPDATE')")
    @PutMapping("/students/{id}/marks")
    public StudentMarksResponse saveMarks(@PathVariable("id") String id,
                                          @Valid @RequestBody MarksRequest request,
                                          @AuthenticationPrincipal StaffPrincipal principal) {
        return marksService.saveMarks(id, request, principal.staffId());
    }

    @PreAuthorize("hasAuthority('MARKS_VIEW')")
    @GetMapping("/marks/highest")
    public HighestMarkResponse highest(@RequestParam(name = "subject") String subject) {
        return marksService.highest(subject);
    }

    @PreAuthorize("hasAuthority('MARKS_VIEW')")
    @GetMapping("/marks/passed")
    public List<StudentResultRow> passed() {
        return marksService.passedStudents();
    }

    @PreAuthorize("hasAuthority('MARKS_VIEW')")
    @GetMapping("/marks/above")
    public List<StudentResultRow> above(@RequestParam(name = "percent", defaultValue = "50") double percent) {
        return marksService.studentsAbove(percent);
    }

    @PreAuthorize("hasAuthority('MARKS_VIEW')")
    @GetMapping("/marks/ranking")
    public List<StudentResultRow> ranking() {
        return marksService.ranking();
    }
}
