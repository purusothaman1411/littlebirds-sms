package com.littlebirds.sms.dto;

import java.time.Instant;

/** Send the token on every request as the header  Authorization: Bearer <token> */
public record LoginResponse(String token, String tokenType, Instant expiresAt, UserProfile user) {
}
