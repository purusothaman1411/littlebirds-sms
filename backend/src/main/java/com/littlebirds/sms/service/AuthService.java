package com.littlebirds.sms.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlebirds.sms.dto.LoginRequest;
import com.littlebirds.sms.dto.LoginResponse;
import com.littlebirds.sms.dto.UserProfile;
import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.exception.InvalidCredentialsException;
import com.littlebirds.sms.repository.StaffUserRepository;
import com.littlebirds.sms.security.JwtService;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final StaffUserRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** Compared against when the username does not exist, so both failures take the same time. */
    private final String dummyHash;

    public AuthService(StaffUserRepository staffRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("not-a-real-password");
    }

    public LoginResponse login(LoginRequest request) {
        Optional<StaffUser> user = staffRepository.findByUsername(request.username().trim());

        // Always check a hash, and give the same message for "no such user" and "wrong password".
        boolean passwordMatches = passwordEncoder.matches(request.password(),
                user.map(StaffUser::getPasswordHash).orElse(dummyHash));

        if (user.isEmpty() || !passwordMatches) {
            throw new InvalidCredentialsException("Invalid Username or Password..!!");
        }

        JwtService.IssuedToken issued = jwtService.issue(user.get());
        return new LoginResponse(issued.token(), "Bearer", issued.expiresAt(), UserProfile.from(user.get()));
    }

    public UserProfile profile(String staffId) {
        return UserProfile.from(staffRepository.findById(staffId)
                .orElseThrow(() -> new InvalidCredentialsException("This account no longer exists")));
    }
}
