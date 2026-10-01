package com.littlebirds.sms.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.dto.LoginRequest;
import com.littlebirds.sms.dto.LoginResponse;
import com.littlebirds.sms.dto.UserProfile;
import com.littlebirds.sms.security.StaffPrincipal;
import com.littlebirds.sms.service.AuthService;

/** There is no server-side session, so "logout" is the client discarding its token. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Any logged-in user: who am I and what may I do. */
    @GetMapping("/me")
    public UserProfile me(@AuthenticationPrincipal StaffPrincipal principal) {
        return authService.profile(principal.staffId());
    }
}
