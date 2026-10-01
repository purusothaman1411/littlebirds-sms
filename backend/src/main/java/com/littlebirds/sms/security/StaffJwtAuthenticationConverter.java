package com.littlebirds.sms.security;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import com.littlebirds.sms.entity.StaffUser;
import com.littlebirds.sms.repository.StaffUserRepository;

/**
 * Runs after the token signature and expiry are verified.
 * It loads the account from the database on every request, so a deleted
 * account or a changed role takes effect immediately instead of when
 * the token expires.
 */
@Component
public class StaffJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final StaffUserRepository staffRepository;

    public StaffJwtAuthenticationConverter(StaffUserRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        StaffUser user = staffRepository.findById(jwt.getSubject())
                .orElseThrow(() ->
                        new InvalidBearerTokenException("This account no longer exists"));

        StaffPrincipal principal = new StaffPrincipal(
                user.getStaffId(),
                user.getUsername(),
                user.getName(),
                user.getRole()
        );

        List<GrantedAuthority> authorities =
                PermissionMatrix.authorityNames(user.getRole())
                        .stream()
                        .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                        .toList();

        return UsernamePasswordAuthenticationToken.authenticated(
                principal,
                null,
                authorities
        );
    }
}