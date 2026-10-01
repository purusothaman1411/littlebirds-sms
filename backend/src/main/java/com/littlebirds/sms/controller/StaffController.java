package com.littlebirds.sms.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.littlebirds.sms.dto.OnCreate;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.PasswordChangeRequest;
import com.littlebirds.sms.dto.StaffRequest;
import com.littlebirds.sms.dto.StaffResponse;
import com.littlebirds.sms.security.StaffPrincipal;
import com.littlebirds.sms.service.StaffService;

/** Staff login accounts (new module; HEADMASTER only through the STAFF_MANAGE permission). */
@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAuthority('STAFF_MANAGE')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public List<StaffResponse> list() {
        return staffService.list();
    }

    @GetMapping("/{id}")
    public StaffResponse get(@PathVariable("id") String id) {
        return staffService.get(id);
    }

    @PostMapping
    public ResponseEntity<StaffResponse> create(@Validated(OnCreate.class) @RequestBody StaffRequest request) {
        StaffResponse created = staffService.create(request);
        return ResponseEntity.created(URI.create("/api/staff/" + created.staffId())).body(created);
    }

    @PutMapping("/{id}")
    public StaffResponse update(@PathVariable("id") String id, @Valid @RequestBody StaffRequest request) {
        return staffService.update(id, request);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable("id") String id,
                                               @Valid @RequestBody PasswordChangeRequest request) {
        staffService.changePassword(id, request.password());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id,
                                       @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.delete(id, principal.staffId());
        return ResponseEntity.noContent().build();
    }
}
