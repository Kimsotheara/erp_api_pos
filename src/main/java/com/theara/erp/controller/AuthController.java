package com.theara.erp.controller;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.request.LoginRequest;
import com.theara.erp.dto.response.DefaultResponse;
import com.theara.erp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Local login and token issuance")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticates a username/password and returns a signed JWT access token.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return DefaultResponse.withCode(authService.login(request), ErrorCode.SUCCESS);
    }

    @Operation(summary = "Current token claims", description = "Returns the claims of the presented JWT (requires erp.security.enabled=true).")
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return DefaultResponse.withCode(null, ErrorCode.UNAUTHORIZED);
        }
        return DefaultResponse.withCode(jwt.getClaims(), ErrorCode.SUCCESS);
    }
}
