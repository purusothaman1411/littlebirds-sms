package com.littlebirds.sms.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.ClassResultsResponse;
import com.littlebirds.sms.dto.ReportCardResponse;
import com.littlebirds.sms.dto.StudentAttendanceResponse;
import com.littlebirds.sms.dto.StudentMarksResponse;
import com.littlebirds.sms.dto.StudentResponse;
import com.littlebirds.sms.dto.StudentResultRow;
import com.littlebirds.sms.entity.Student;

/** Reports are computed from students, marks and attendance; nothing is stored. */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final StudentService studentService;
    private final MarksService marksService;
    private final AttendanceService attendanceService;

    public ReportService(StudentService studentService, MarksService marksService,
                         AttendanceService attendanceService) {
        this.studentService = studentService;
        this.marksService = marksService;
        this.attendanceService = attendanceService;
    }

    public ReportCardResponse reportCard(String studentId) {
        Student student = studentService.getEntity(studentId);
        StudentMarksResponse marks = marksService.getMarks(student.getStudentId());
        StudentAttendanceResponse attendance = attendanceService.getStudentAttendance(student.getStudentId());
        return new ReportCardResponse(StudentResponse.from(student), marks, attendance.summary());
    }

    /** Results grouped by standard; pass a standard to get just that class. */
    public List<ClassResultsResponse> classResults(Integer standard) {
        Map<Integer, List<StudentResultRow>> byStandard = marksService.resultRows().stream()
                .filter(r -> standard == null || standard.equals(r.standard()))
                .collect(Collectors.groupingBy(StudentResultRow::standard, TreeMap::new, Collectors.toList()));

        return byStandard.entrySet().stream()
                .map(e -> new ClassResultsResponse(e.getKey(), e.getValue()))
                .toList();
    }
}
