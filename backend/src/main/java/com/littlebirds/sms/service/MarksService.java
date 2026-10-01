package com.littlebirds.sms.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.HighestMarkResponse;
import com.littlebirds.sms.dto.MarksRequest;
import com.littlebirds.sms.dto.ResultSummary;
import com.littlebirds.sms.dto.StudentMarksResponse;
import com.littlebirds.sms.dto.StudentResultRow;
import com.littlebirds.sms.dto.SubjectMarkDto;
import com.littlebirds.sms.entity.Mark;
import com.littlebirds.sms.entity.Role;
import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.entity.Student;
import com.littlebirds.sms.entity.Subject;
import com.littlebirds.sms.exception.ForbiddenOperationException;
import com.littlebirds.sms.exception.InvalidMarkException;
import com.littlebirds.sms.exception.InvalidRequestException;
import com.littlebirds.sms.exception.MarksNotFoundException;
import com.littlebirds.sms.repository.MarkRepository;
import com.littlebirds.sms.repository.StaffUserRepository;
import com.littlebirds.sms.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class MarksService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final StaffUserRepository staffRepository;
    private final StudentService studentService;
    private final CurriculumService curriculumService;

    public MarksService(MarkRepository markRepository, StudentRepository studentRepository,
                        StaffUserRepository staffRepository, StudentService studentService,
                        CurriculumService curriculumService) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.studentService = studentService;
        this.curriculumService = curriculumService;
    }

    /** Marks, missing subjects and the overall result of one student. */
    public StudentMarksResponse getMarks(String studentId) {
        Student student = studentService.getEntity(studentId);
        List<Subject> curriculum = curriculumService.subjectsFor(student.getStandard(), student.getGroupName());
        return build(student, markRepository.findByStudent_StudentId(student.getStudentId()), curriculum);
    }

    /**
     * Adds or updates marks (replaces the console's separate "Add Marks" and "Update Marks").
     * Everything is validated first; nothing is saved if any entry is invalid.
     * HEADMASTER may enter any subject; SUBJECT_STAFF only their assigned subjects (matched by name).
     */
    @Transactional
    public StudentMarksResponse saveMarks(String studentId, MarksRequest request, String actingStaffId) {
        Student student = studentService.getEntity(studentId);

        StaffUser actor = staffRepository.findById(actingStaffId)
                .orElseThrow(() -> new ForbiddenOperationException("Unknown staff account"));
        if (actor.getRole() != Role.HEADMASTER && actor.getRole() != Role.SUBJECT_STAFF) {
            throw new ForbiddenOperationException("Access Denied..!! Your role cannot add or update marks.");
        }
        if (request.marks() == null || request.marks().isEmpty()) {
            throw new InvalidRequestException("At least one subject mark is required");
        }

        List<Subject> curriculum = curriculumService.subjectsFor(student.getStandard(), student.getGroupName());
        Map<String, Subject> curriculumByName = curriculum.stream()
                .collect(Collectors.toMap(s -> key(s.getName()), s -> s));

        Set<String> assigned = actor.getRole() == Role.SUBJECT_STAFF
                ? actor.getSubjects().stream().map(s -> key(s.getName())).collect(Collectors.toSet())
                : null;

        // 1. validate everything
        Map<Subject, Integer> toSave = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : request.marks().entrySet()) {
            String subjectKey = key(entry.getKey());
            Subject subject = curriculumByName.get(subjectKey);
            if (subject == null) {
                throw new InvalidRequestException(
                        "Subject '" + entry.getKey() + "' is not in this student's curriculum");
            }
            Integer mark = entry.getValue();
            if (mark == null || mark < 0 || mark > ResultCalculator.MAX_MARK) {
                throw new InvalidMarkException("Invalid Mark..!! Mark should be between 0 and 100.");
            }
            if (assigned != null && !assigned.contains(subjectKey)) {
                throw new ForbiddenOperationException(
                        "Access Denied..!! You are not assigned to " + subject.getName() + ".");
            }
            toSave.put(subject, mark);
        }

        // 2. save
        for (Map.Entry<Subject, Integer> entry : toSave.entrySet()) {
            Subject subject = entry.getKey();
            int value = entry.getValue();
            Mark mark = markRepository
                    .findByStudent_StudentIdAndSubject_Id(student.getStudentId(), subject.getId())
                    .orElseGet(() -> new Mark(student, subject, value, actor));
            mark.setMark(value);
            mark.setEnteredBy(actor);
            markRepository.save(mark);
        }

        return build(student, markRepository.findByStudent_StudentId(student.getStudentId()), curriculum);
    }

    /** Console feature "Highest Score" for one subject. */
    public HighestMarkResponse highest(String subjectName) {
        Mark top = markRepository
                .findFirstBySubject_NameIgnoreCaseOrderByMarkDescStudent_StudentIdAsc(subjectName.trim())
                .orElseThrow(() -> new MarksNotFoundException("No Marks Found for this Subject..!!"));
        return new HighestMarkResponse(top.getStudent().getStudentId(), top.getStudent().getName(),
                top.getSubject().getName(), top.getMark());
    }

    /** Students whose marks are complete and all at least 35. */
    public List<StudentResultRow> passedStudents() {
        return resultRows().stream()
                .filter(r -> ResultCalculator.PASS.equals(r.result()))
                .toList();
    }

    /** Students with complete marks and a percentage above the limit (console: above 50). */
    public List<StudentResultRow> studentsAbove(double percent) {
        return resultRows().stream()
                .filter(r -> r.complete() && r.percentage() > percent)
                .toList();
    }

    /** All students by total marks entered, highest first (ties by student ID). */
    public List<StudentResultRow> ranking() {
        return resultRows().stream()
                .sorted(Comparator.comparingInt(StudentResultRow::total).reversed()
                        .thenComparing(StudentResultRow::studentId, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /** One result row per student, ordered by standard then ID. Used by reports too. */
    public List<StudentResultRow> resultRows() {
        List<Student> students = studentRepository.findAllByOrderByStandardAscStudentIdAsc();
        Map<String, List<Mark>> marksByStudent = markRepository.findAllWithStudent().stream()
                .collect(Collectors.groupingBy(m -> m.getStudent().getStudentId()));

        Map<String, List<Subject>> curriculumCache = new HashMap<>();
        List<StudentResultRow> rows = new ArrayList<>();

        for (Student student : students) {
            String cacheKey = student.getStandard() <= 10 ? "1-10" : "11-12|" + student.getGroupName();
            List<Subject> curriculum = curriculumCache.computeIfAbsent(cacheKey,
                    k -> curriculumService.subjectsFor(student.getStandard(), student.getGroupName()));

            StudentMarksResponse marks = build(student,
                    marksByStudent.getOrDefault(student.getStudentId(), List.of()), curriculum);
            ResultSummary s = marks.summary();
            rows.add(new StudentResultRow(student.getStudentId(), student.getName(), student.getStandard(),
                    student.getGroupName(), s.total(), s.percentage(), s.grade(), s.result(), s.complete()));
        }
        return rows;
    }

    private StudentMarksResponse build(Student student, List<Mark> marks, List<Subject> curriculum) {
        Map<Long, Integer> markBySubjectId = marks.stream()
                .collect(Collectors.toMap(m -> m.getSubject().getId(), Mark::getMark, (a, b) -> b));

        List<SubjectMarkDto> entered = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Subject subject : curriculum) {
            Integer value = markBySubjectId.get(subject.getId());
            if (value == null) {
                missing.add(subject.getName());
            } else {
                entered.add(new SubjectMarkDto(subject.getName(), value));
                values.add(value);
            }
        }

        return new StudentMarksResponse(student.getStudentId(), student.getName(), student.getStandard(),
                student.getGroupName(), entered, missing, ResultCalculator.summarize(values, curriculum.size()));
    }

    private static String key(String subjectName) {
        return subjectName.trim().toLowerCase(Locale.ROOT);
    }
}
