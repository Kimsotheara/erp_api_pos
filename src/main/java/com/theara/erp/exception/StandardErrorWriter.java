package com.theara.erp.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.response.NormalizeResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public final class StandardErrorWriter {
    private StandardErrorWriter() {
    }

    public static void write(HttpServletResponse response, ObjectMapper objectMapper, ErrorCode code) throws IOException {
        write(response, objectMapper, code, code.getDescription());
    }

    public static void write(HttpServletResponse response, ObjectMapper objectMapper,
                             ErrorCode code, String message) throws IOException {
        NormalizeResponse<Object> body = new NormalizeResponse<>(null);
        body.setResult(false);
        body.setResultCode(code.getCode());
        body.setResultMessage(message);

        response.setStatus(code.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
