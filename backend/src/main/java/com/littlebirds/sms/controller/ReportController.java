package com.littlebirds.sms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.ClassResultsResponse;
import com.littlebirds.sms.dto.ReportCardResponse;
import com.littlebirds.sms.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    @GetMapping("/report-card/{studentId}")
    public ReportCardResponse reportCard(@PathVariable("studentId") String studentId) {
        return reportService.reportCard(studentId);
    }

    /** All classes, or one class when standard is given. */
    @PreAuthorize("hasAuthority('REPORT_CLASS_VIEW')")
    @GetMapping("/class-results")
    public List<ClassResultsResponse> classResults(
            @RequestParam(name = "standard", required = false) Integer standard) {
        return reportService.classResults(standard);
    }
}
