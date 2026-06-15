package com.theara.erp.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theara.erp.constant.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Returns the standard envelope (resultCode 0005) when a request has no / an invalid token. */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        StandardErrorWriter.write(response, objectMapper, ErrorCode.UNAUTHORIZED);
    }
}
