package com.littlebirds.sms.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.AttendanceEntry;
import com.littlebirds.sms.dto.AttendanceRecordDto;
import com.littlebirds.sms.dto.AttendanceRequest;
import com.littlebirds.sms.dto.AttendanceSaveResponse;
import com.littlebirds.sms.dto.AttendanceSummary;
import com.littlebirds.sms.dto.StudentAttendanceResponse;
import com.littlebirds.sms.entity.AttendanceRecord;
import com.littlebirds.sms.entity.AttendanceStatus;
import com.littlebirds.sms.entity.Role;
import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.entity.Student;
import com.littlebirds.sms.exception.ForbiddenOperationException;
import com.littlebirds.sms.exception.InvalidRequestException;
import com.littlebirds.sms.repository.AttendanceRepository;
import com.littlebirds.sms.repository.StaffUserRepository;
import com.littlebirds.sms.repository.StudentRepository;

/**
 * Per-date attendance (your Attend plan): a class is marked for one date, every student of the class
 * must be included, there are no future dates, and saving again for the same date updates the records.
 */
@Service
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final StaffUserRepository staffRepository;
    private final StudentService studentService;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository,
                             StaffUserRepository staffRepository, StudentService studentService) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.studentService = studentService;
    }

    @Transactional
    public AttendanceSaveResponse markAttendance(AttendanceRequest request, String actingStaffId) {
        StaffUser actor = staffRepository.findById(actingStaffId)
                .orElseThrow(() -> new ForbiddenOperationException("Unknown staff account"));
        if (actor.getRole() == Role.MANAGEMENT_STAFF) {
            throw new ForbiddenOperationException("Access Denied..!! Your role cannot mark attendance.");
        }

        LocalDate date = ValidationRules.requireNotFuture(request.date(), "Attendance date");
        Integer standard = request.standard();
        if (standard == null || standard < 1 || standard > 12) {
            throw new InvalidRequestException("Standard must be between 1 and 12");
        }
        if (request.entries() == null || request.entries().isEmpty()) {
            throw new InvalidRequestException("Attendance entries are required");
        }

        List<Student> classStudents = studentRepository.findByStandardOrderByStudentId(standard);
        if (classStudents.isEmpty()) {
            throw new InvalidRequestException("There are no students in standard " + standard);
        }
        Map<String, Student> studentsById = classStudents.stream()
                .collect(Collectors.toMap(s -> key(s.getStudentId()), s -> s));

        // validate the submitted list
        Map<String, AttendanceStatus> submitted = new LinkedHashMap<>();
        for (AttendanceEntry entry : request.entries()) {
            if (entry.studentId() == null || entry.studentId().isBlank()) {
                throw new InvalidRequestException("Student ID is required for every attendance entry");
            }
            String id = key(entry.studentId());
            if (!studentsById.containsKey(id)) {
                throw new InvalidRequestException(
                        "Student " + entry.studentId().trim() + " is not in standard " + standard);
            }
            if (submitted.containsKey(id)) {
                throw new InvalidRequestException("Student " + entry.studentId().trim() + " appears more than once");
            }
            submitted.put(id, parseStatus(entry.status()));
        }

        List<String> missing = studentsById.entrySet().stream()
                .filter(e -> !submitted.containsKey(e.getKey()))
                .map(e -> e.getValue().getStudentId())
                .toList();
        if (!missing.isEmpty()) {
            throw new InvalidRequestException(
                    "Attendance must be marked for every student in the class. Missing: " + String.join(", ", missing));
        }

        // save: create new records, update existing ones for the same date
        Map<String, AttendanceRecord> existing = attendanceRepository
                .findByStudent_StandardAndAttendanceDate(standard, date).stream()
                .collect(Collectors.toMap(r -> key(r.getStudent().getStudentId()), r -> r));

        int created = 0;
        int updated = 0;
        int present = 0;
        int absent = 0;

        for (Map.Entry<String, AttendanceStatus> entry : submitted.entrySet()) {
            AttendanceStatus status = entry.getValue();
            AttendanceRecord record = existing.get(entry.getKey());

            if (record == null) {
                record = new AttendanceRecord(studentsById.get(entry.getKey()), date, status, actor);
                created++;
            } else {
                record.setStatus(status);
                record.setMarkedBy(actor);
                updated++;
            }
            attendanceRepository.save(record);

            if (status == AttendanceStatus.PRESENT) present++; else absent++;
        }

        return new AttendanceSaveResponse(date, standard, created, updated, present, absent);
    }

    /** History and percentage for one student. */
    public StudentAttendanceResponse getStudentAttendance(String studentId) {
        Student student = studentService.getEntity(studentId);
        List<AttendanceRecord> records = attendanceRepository
                .findByStudent_StudentIdOrderByAttendanceDate(student.getStudentId());

        int present = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        int absent = records.size() - present;

        return new StudentAttendanceResponse(student.getStudentId(), student.getName(),
                summary(present, absent), records.stream().map(AttendanceRecordDto::from).toList());
    }

    /** Attendance of one class on one date. */
    public List<AttendanceRecordDto> getClassAttendance(int standard, LocalDate date) {
        return attendanceRepository.findByStudent_StandardAndAttendanceDate(standard, date).stream()
                .map(AttendanceRecordDto::from)
                .sorted((a, b) -> a.studentId().compareToIgnoreCase(b.studentId()))
                .toList();
    }

    /** Attendance of the whole school on one date. */
    public List<AttendanceRecordDto> getDailyAttendance(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date).stream()
                .map(AttendanceRecordDto::from)
                .sorted((a, b) -> a.studentId().compareToIgnoreCase(b.studentId()))
                .toList();
    }

    /** Percentage = present / (present + absent), as in the console's Attendance class. */
    public AttendanceSummary summary(int present, int absent) {
        int total = present + absent;
        double percentage = total == 0 ? 0 : ResultCalculator.round2(present * 100.0 / total);
        return new AttendanceSummary(present, absent, total, percentage);
    }

    private AttendanceStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("Attendance status is required (P or A)");
        }
        try {
            return AttendanceStatus.fromCode(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid Attendance! Enter P for Present or A for Absent.");
        }
    }

    private static String key(String studentId) {
        return studentId.trim().toLowerCase(Locale.ROOT);
    }
}
