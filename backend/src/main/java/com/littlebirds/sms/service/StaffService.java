package com.littlebirds.sms.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.StaffRequest;
import com.littlebirds.sms.dto.StaffResponse;
import com.littlebirds.sms.entity.Role;
import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.entity.Subject;
import com.littlebirds.sms.exception.InvalidRequestException;
import com.littlebirds.sms.exception.StaffAlreadyExistsException;
import com.littlebirds.sms.exception.StaffNotFoundException;
import com.littlebirds.sms.repository.StaffUserRepository;
import com.littlebirds.sms.repository.SubjectRepository;

/**
 * Staff login accounts. This module is new (the console app had no staff management);
 * access is limited to the HEADMASTER in Phase 9.
 */
@Service
@Transactional(readOnly = true)
public class StaffService {

    private static final List<String> CLASS_RANGES = List.of("ALL", "1-10", "11-12");

    private final StaffUserRepository staffRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffService(StaffUserRepository staffRepository, SubjectRepository subjectRepository,
                        PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StaffResponse> list() {
        return staffRepository.findAll().stream()
                .sorted((a, b) -> a.getStaffId().compareToIgnoreCase(b.getStaffId()))
                .map(StaffResponse::from)
                .toList();
    }

    public StaffResponse get(String id) {
        return StaffResponse.from(getEntity(id));
    }

    @Transactional
    public StaffResponse create(StaffRequest request) {
        String id = request.staffId().trim();
        String username = request.username().trim();

        if (staffRepository.existsById(id)) {
            throw new StaffAlreadyExistsException("Staff ID already exists");
        }
        if (staffRepository.existsByUsername(username)) {
            throw new StaffAlreadyExistsException("Username already exists");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new InvalidRequestException("Password is required");
        }

        StaffUser user = new StaffUser(id, request.name().trim(), username,
                passwordEncoder.encode(request.password()), parseRole(request.role()));
        applyAssignment(user, request);

        return StaffResponse.from(staffRepository.save(user));
    }

    @Transactional
    public StaffResponse update(String id, StaffRequest request) {
        StaffUser user = getEntity(id);
        String username = request.username().trim();

        if (!user.getUsername().equalsIgnoreCase(username) && staffRepository.existsByUsername(username)) {
            throw new StaffAlreadyExistsException("Username already exists");
        }

        Role newRole = parseRole(request.role());
        if (user.getRole() == Role.HEADMASTER && newRole != Role.HEADMASTER) {
            requireAnotherHeadmaster();
        }

        user.setName(request.name().trim());
        user.setUsername(username);
        user.setRole(newRole);
        applyAssignment(user, request);

        // A blank password means "keep the current one".
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return StaffResponse.from(staffRepository.save(user));
    }

    @Transactional
    public void changePassword(String id, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new InvalidRequestException("Password is required");
        }
        StaffUser user = getEntity(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        staffRepository.save(user);
    }

    @Transactional
    public void delete(String id, String actingStaffId) {
        StaffUser user = getEntity(id);

        if (user.getStaffId().equalsIgnoreCase(actingStaffId)) {
            throw new InvalidRequestException("You cannot delete your own account");
        }
        if (user.getRole() == Role.HEADMASTER) {
            requireAnotherHeadmaster();
        }
        staffRepository.delete(user);
    }

    /** Shared with other services that need the entity. */
    public StaffUser getEntity(String id) {
        return staffRepository.findById(id.trim())
                .orElseThrow(() -> new StaffNotFoundException("Staff Not Found..!!"));
    }

    private void requireAnotherHeadmaster() {
        if (staffRepository.countByRole(Role.HEADMASTER) <= 1) {
            throw new InvalidRequestException("The last HEADMASTER account cannot be removed or changed to another role");
        }
    }

    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException("Role is required");
        }
        try {
            return Role.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid role. Use HEADMASTER, SUBJECT_STAFF, WORKING_STAFF or MANAGEMENT_STAFF");
        }
    }

    /** Class range, group and subject assignment (stored as in the console's staff.txt). */
    private void applyAssignment(StaffUser user, StaffRequest request) {
        String range = (request.classRange() == null || request.classRange().isBlank())
                ? "ALL" : request.classRange().trim().toUpperCase(Locale.ROOT);
        if (!CLASS_RANGES.contains(range)) {
            throw new InvalidRequestException("Class range must be one of: " + String.join(", ", CLASS_RANGES));
        }
        user.setClassRange(range);

        String group = request.group();
        if (group == null || group.isBlank() || "N/A".equalsIgnoreCase(group.trim())) {
            user.setGroupName(null);
        } else {
            user.setGroupName(CurriculumService.GROUPS.stream()
                    .filter(g -> g.equalsIgnoreCase(group.trim()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidRequestException(
                            "Invalid group. Use one of: " + String.join(", ", CurriculumService.GROUPS))));
        }

        Set<Subject> subjects = new LinkedHashSet<>();
        if (request.subjects() != null) {
            for (String name : request.subjects()) {
                if (name == null || name.isBlank() || "ALL".equalsIgnoreCase(name.trim())) {
                    continue; // "ALL" means no restriction, which is an empty assignment
                }
                subjects.add(subjectRepository.findByNameIgnoreCase(name.trim())
                        .orElseThrow(() -> new InvalidRequestException("Unknown subject: " + name.trim())));
            }
        }
        user.setSubjects(subjects);
    }
}
