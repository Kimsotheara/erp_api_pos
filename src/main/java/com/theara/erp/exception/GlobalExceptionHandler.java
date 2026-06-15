package com.theara.erp.exception;

import com.theara.erp.constant.ErrorCode;
import com.theara.erp.dto.response.NormalizeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NormalizeResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("[ VALIDATION ERROR ] fields = {}", fieldErrors);
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<NormalizeResponse<Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                "Missing required parameter '" + ex.getParameterName() + "'", null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<NormalizeResponse<Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("[ UNREADABLE BODY ] {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, "Malformed or missing request body", null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<NormalizeResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
        log.warn("[ REQUEST ERROR ] status = {}, message = {}", ex.getStatusCode(), ex.getReason());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorCode errorCode = resolveErrorCode(status.value());
        String message = ex.getReason() != null ? ex.getReason() : errorCode.getDescription();
        return build(status, errorCode, message, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<NormalizeResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[ ILLEGAL ARGUMENT ] message = {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NormalizeResponse<Object>> handleGeneric(Exception ex) {
        log.error("[ UNEXPECTED ERROR ] message = {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDescription(), null);
    }

    private ResponseEntity<NormalizeResponse<Object>> build(HttpStatus status, ErrorCode code, String message, Object body) {
        NormalizeResponse<Object> response = new NormalizeResponse<>(body);
        response.setResult(false);
        response.setResultCode(code.getCode());
        response.setResultMessage(message);
        return ResponseEntity.status(status).body(response);
    }

    private ErrorCode resolveErrorCode(int status) {
        return switch (status) {
            case 404 -> ErrorCode.NOT_FOUND;
            case 409 -> ErrorCode.CONFLICT;
            case 400 -> ErrorCode.BAD_REQUEST;
            case 401 -> ErrorCode.UNAUTHORIZED;
            case 403 -> ErrorCode.FORBIDDEN;
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            case 415 -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> ErrorCode.INTERNAL_SERVER_ERROR;
        };
    }
}
