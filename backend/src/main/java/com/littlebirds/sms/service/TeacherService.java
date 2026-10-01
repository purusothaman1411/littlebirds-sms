package com.littlebirds.sms.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.PageResponse;
import com.littlebirds.sms.dto.TeacherRequest;
import com.littlebirds.sms.dto.TeacherResponse;
import com.littlebirds.sms.entity.Teacher;
import com.littlebirds.sms.exception.InvalidRequestException;
import com.littlebirds.sms.exception.TeacherAlreadyExistsException;
import com.littlebirds.sms.exception.TeacherNotFoundException;
import com.littlebirds.sms.repository.TeacherRepository;

@Service
@Transactional(readOnly = true)
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        String id = request.teacherId().trim();

        if (teacherRepository.existsById(id)) {
            throw new TeacherAlreadyExistsException("Teacher ID already exists..!!");
        }

        Teacher teacher = new Teacher(
                id,
                request.name().trim(),
                ValidationRules.requireNotFuture(request.dob(), "Date of birth"),
                ValidationRules.parseGender(request.gender()),
                request.qualification().trim(),
                request.email().trim(),
                request.contactNumber().trim(),
                request.address().trim(),
                cleanSubjects(request.subjects()));

        return TeacherResponse.from(teacherRepository.save(teacher));
    }

    public TeacherResponse get(String id) {
        return TeacherResponse.from(getEntity(id));
    }

    /** Lists teachers; a subject filter gives the console's "Search By Subject". */
    public PageResponse<TeacherResponse> search(String subject, int page, int size) {
        String subjectFilter = (subject == null || subject.isBlank()) ? null : subject.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by("teacherId"));
        Page<Teacher> result = teacherRepository.search(subjectFilter, pageable);
        return PageResponse.from(result.map(TeacherResponse::from));
    }

    /** Console feature "Teachers More Than 2 Subjects". */
    public List<TeacherResponse> withMoreThanTwoSubjects() {
        return teacherRepository.findWithMoreThanTwoSubjects().stream()
                .map(TeacherResponse::from)
                .toList();
    }

    @Transactional
    public TeacherResponse update(String id, TeacherRequest request) {
        Teacher teacher = getEntity(id);

        teacher.setName(request.name().trim());
        teacher.setDob(ValidationRules.requireNotFuture(request.dob(), "Date of birth"));
        teacher.setGender(ValidationRules.parseGender(request.gender()));
        teacher.setQualification(request.qualification().trim());
        teacher.setEmail(request.email().trim());
        teacher.setContactNumber(request.contactNumber().trim());
        teacher.setAddress(request.address().trim());
        teacher.setSubjects(cleanSubjects(request.subjects()));

        return TeacherResponse.from(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(String id) {
        if (!teacherRepository.existsById(id)) {
            throw new TeacherNotFoundException("Teacher Not Found..!!");
        }
        teacherRepository.deleteById(id);
    }

    private Teacher getEntity(String id) {
        return teacherRepository.findById(id.trim())
                .orElseThrow(() -> new TeacherNotFoundException("Teacher Not Found..!!"));
    }

    /** Trims, drops blanks and case-insensitive duplicates; at least one subject is required. */
    private Set<String> cleanSubjects(Collection<String> subjects) {
        Map<String, String> unique = new LinkedHashMap<>();
        if (subjects != null) {
            for (String subject : subjects) {
                if (subject != null && !subject.isBlank()) {
                    unique.putIfAbsent(subject.trim().toLowerCase(Locale.ROOT), subject.trim());
                }
            }
        }
        if (unique.isEmpty()) {
            throw new InvalidRequestException("At least one subject is required");
        }
        return new LinkedHashSet<>(unique.values());
    }
}
