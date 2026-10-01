package com.littlebirds.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.entity.CurriculumEntry;
import com.littlebirds.sms.entity.CurriculumLevel;
import com.littlebirds.sms.entity.Subject;
import com.littlebirds.sms.exception.InvalidRequestException;
import com.littlebirds.sms.repository.CurriculumRepository;

/** Which subjects a student takes, and which standard/group combinations are valid. */
@Service
@Transactional(readOnly = true)
public class CurriculumService {

    public static final List<String> GROUPS = List.of("Bio-Maths", "Computer Science", "Commerce", "Humanities");

    private final CurriculumRepository curriculumRepository;

    public CurriculumService(CurriculumRepository curriculumRepository) {
        this.curriculumRepository = curriculumRepository;
    }

    /**
     * Validates the standard/group pair and returns the group in its stored spelling:
     * null for standards 1-10, one of GROUPS for 11-12.
     */
    public String normalizeGroup(Integer standard, String group) {
        if (standard == null || standard < 1 || standard > 12) {
            throw new InvalidRequestException("Standard must be between 1 and 12");
        }

        boolean blank = group == null || group.isBlank() || "N/A".equalsIgnoreCase(group.trim());

        if (standard <= 10) {
            if (!blank) {
                throw new InvalidRequestException("Group applies only to standards 11 and 12");
            }
            return null;
        }

        if (blank) {
            throw new InvalidRequestException("Group is required for standards 11 and 12");
        }
        return GROUPS.stream()
                .filter(g -> g.equalsIgnoreCase(group.trim()))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(
                        "Invalid group. Use one of: " + String.join(", ", GROUPS)));
    }

    public List<Subject> subjectsFor(int standard, String group) {
        CurriculumLevel level = CurriculumLevel.forStandard(standard);
        String groupName = group == null ? CurriculumEntry.NO_GROUP : group;
        return curriculumRepository.findSubjects(level, groupName);
    }

    public List<String> subjectNamesFor(int standard, String group) {
        return subjectsFor(standard, group).stream().map(Subject::getName).toList();
    }
}
