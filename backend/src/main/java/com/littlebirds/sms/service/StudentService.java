package com.littlebirds.sms.service;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.PageResponse;
import com.littlebirds.sms.dto.StudentRequest;
import com.littlebirds.sms.dto.StudentResponse;
import com.littlebirds.sms.entity.CurriculumLevel;
import com.littlebirds.sms.entity.Student;
import com.littlebirds.sms.exception.StudentAlreadyExistsException;
import com.littlebirds.sms.exception.StudentNotFoundException;
import com.littlebirds.sms.repository.MarkRepository;
import com.littlebirds.sms.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final MarkRepository markRepository;
    private final CurriculumService curriculumService;

    public StudentService(StudentRepository studentRepository, MarkRepository markRepository,
                          CurriculumService curriculumService) {
        this.studentRepository = studentRepository;
        this.markRepository = markRepository;
        this.curriculumService = curriculumService;
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        String id = request.studentId().trim();

        // IDs are case-insensitive (as in the console app), so check before saving.
        if (studentRepository.existsById(id)) {
            throw new StudentAlreadyExistsException("Student ID already exists!");
        }

        String group = curriculumService.normalizeGroup(request.standard(), request.group());

        Student student = new Student(
                id,
                request.name().trim(),
                ValidationRules.requireNotFuture(request.dob(), "Date of birth"),
                ValidationRules.parseGender(request.gender()),
                request.standard(),
                group,
                request.address().trim(),
                request.contactNumber().trim());

        return StudentResponse.from(studentRepository.save(student));
    }

    public StudentResponse get(String id) {
        return StudentResponse.from(getEntity(id));
    }

    public PageResponse<StudentResponse> search(String name, Integer standard, int page, int size) {
        String nameFilter = (name == null || name.isBlank()) ? null : name.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by("studentId"));
        Page<Student> result = studentRepository.search(nameFilter, standard, pageable);
        return PageResponse.from(result.map(StudentResponse::from));
    }

    @Transactional
    public StudentResponse update(String id, StudentRequest request) {
        Student student = getEntity(id);

        int oldStandard = student.getStandard();
        String oldGroup = student.getGroupName();

        String newGroup = curriculumService.normalizeGroup(request.standard(), request.group());

        student.setName(request.name().trim());
        student.setDob(ValidationRules.requireNotFuture(request.dob(), "Date of birth"));
        student.setGender(ValidationRules.parseGender(request.gender()));
        student.setStandard(request.standard());
        student.setGroupName(newGroup);
        student.setAddress(request.address().trim());
        student.setContactNumber(request.contactNumber().trim());

        boolean curriculumChanged =
                CurriculumLevel.forStandard(oldStandard) != CurriculumLevel.forStandard(request.standard())
                        || !Objects.equals(oldGroup, newGroup);
        if (curriculumChanged) {
            removeMarksOutsideCurriculum(student);
        }

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public void delete(String id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student Not Found..!!");
        }
        // Marks and attendance are removed by ON DELETE CASCADE in the database.
        studentRepository.deleteById(id);
    }

    /** Shared by other services that need the entity. */
    public Student getEntity(String id) {
        return studentRepository.findById(id.trim())
                .orElseThrow(() -> new StudentNotFoundException("Student Not Found..!!"));
    }

    /**
     * When a student moves to a class or group with a different curriculum, marks for subjects
     * that are no longer taken would be meaningless, so they are removed.
     */
    private void removeMarksOutsideCurriculum(Student student) {
        Set<String> allowed = curriculumService.subjectNamesFor(student.getStandard(), student.getGroupName())
                .stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        var stale = markRepository.findByStudent_StudentId(student.getStudentId()).stream()
                .filter(m -> !allowed.contains(m.getSubject().getName().toLowerCase(Locale.ROOT)))
                .toList();
        markRepository.deleteAll(stale);
    }
}
