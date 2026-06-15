package com.theara.erp.service;

import com.theara.erp.entity.Role;
import com.theara.erp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/** Issues short-lived HS256 access tokens carrying the user's identity and roles. */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;

    @Value("${erp.security.jwt.access-token-ttl-seconds:3600}")
    private long ttlSeconds;

    public String generateToken(User user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("erp-pos-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .subject(user.getUsername())
                .claim("uid", user.getId())
                .claim("companyId", user.getCompany().getId())
                .claim("fullName", user.getFullName())
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}
