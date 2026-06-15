package com.theara.erp.service.impl;

import com.theara.erp.dto.request.LoginRequest;
import com.theara.erp.dto.response.LoginResponse;
import com.theara.erp.entity.User;
import com.theara.erp.mapper.UserMapper;
import com.theara.erp.repository.UserRepository;
import com.theara.erp.service.AuthService;
import com.theara.erp.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j @Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = (request.getCompanyId() != null
                ? userRepository.findByCompanyIdAndUsernameIgnoreCase(request.getCompanyId(), request.getUsername())
                : userRepository.findFirstByUsernameIgnoreCase(request.getUsername()))
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw invalidCredentials();
        }
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getTtlSeconds())
                .user(userMapper.toResponse(user))
                .build();
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
