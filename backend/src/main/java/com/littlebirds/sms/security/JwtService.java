package com.littlebirds.sms.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.littlebirds.sms.entity.StaffUser;

@Service
public class JwtService {

    public record IssuedToken(String token, Instant expiresAt) {
    }

    private final JwtEncoder encoder;
    private final long expiryMinutes;

    public JwtService(JwtEncoder encoder, @Value("${sms.jwt.expiry-minutes:480}") long expiryMinutes) {
        this.encoder = encoder;
        this.expiryMinutes = expiryMinutes;
    }

    /** The token only identifies the user (subject = staff ID); permissions are loaded from the database per request. */
    public IssuedToken issue(StaffUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(expiryMinutes));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("littlebirds-sms")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getStaffId())
                .claim("role", user.getRole().name())
                .build();

        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
        return new IssuedToken(token, expiresAt);
    }
}
