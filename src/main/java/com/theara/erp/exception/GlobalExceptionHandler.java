package com.theara.erp.exception;

import com.theara.erp.constant.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("[ VALIDATION ERROR ] fields = {}", fieldErrors);
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        log.error("[ REQUEST ERROR ] status = {}, message = {}", ex.getStatusCode(), ex.getReason());
        ErrorCode errorCode = resolveErrorCode(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : errorCode.getDescription();
        return buildResponse(HttpStatus.valueOf(ex.getStatusCode().value()), errorCode, message, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("[ ILLEGAL ARGUMENT ] message = {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("[ UNEXPECTED ERROR ] message = {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, ErrorCode errorCode, String message, Object body) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("result", false);
        response.put("resultCode", errorCode.getCode());
        response.put("resultMessage", message);
        response.put("body", body);
        return ResponseEntity.status(status).body(response);
    }

    private ErrorCode resolveErrorCode(int status) {
        return switch (status) {
            case 404 -> ErrorCode.NOT_FOUND;
            case 409 -> ErrorCode.CONFLICT;
            case 400 -> ErrorCode.BAD_REQUEST;
            case 401 -> ErrorCode.UNAUTHORIZED;
            case 403 -> ErrorCode.FORBIDDEN;
            default -> ErrorCode.INTERNAL_SERVER_ERROR;
        };
    }
}
