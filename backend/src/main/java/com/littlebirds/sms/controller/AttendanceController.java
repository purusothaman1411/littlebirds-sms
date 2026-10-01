package com.littlebirds.sms.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.AttendanceRecordDto;
import com.littlebirds.sms.dto.AttendanceRequest;
import com.littlebirds.sms.dto.AttendanceSaveResponse;
import com.littlebirds.sms.dto.StudentAttendanceResponse;
import com.littlebirds.sms.security.StaffPrincipal;
import com.littlebirds.sms.service.AttendanceService;

/**
 * Per-date attendance. Dates are ISO (yyyy-MM-dd).
 */
@RestController
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /** Marks (or re-marks) a whole class for one date. */
    @PreAuthorize("hasAuthority('ATTENDANCE_MARK')")
    @PostMapping("/attendance")
    public AttendanceSaveResponse mark(@Valid @RequestBody AttendanceRequest request,
                                       @AuthenticationPrincipal StaffPrincipal principal) {
        return attendanceService.markAttendance(request, principal.staffId());
    }

    /** One date for the whole school, or for one class when standard is given. */
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @GetMapping("/attendance")
    public List<AttendanceRecordDto> byDate(
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "standard", required = false) Integer standard) {
        return standard == null
                ? attendanceService.getDailyAttendance(date)
                : attendanceService.getClassAttendance(standard, date);
    }

    /** History and percentage of one student. */
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    @GetMapping("/students/{id}/attendance")
    public StudentAttendanceResponse studentAttendance(@PathVariable("id") String id) {
        return attendanceService.getStudentAttendance(id);
    }
}
