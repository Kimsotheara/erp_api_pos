package com.theara.erp.exception;

import com.theara.erp.constant.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Factory for the {@link ResponseStatusException}s thrown throughout the services,
 * so the same status + message conventions live in one place instead of being
 * repeated as a private {@code notFound(...)} helper in every service.
 */
public final class ApiException {

    private ApiException() {
    }

    /** 404 — "<Entity> Resource not found". */
    public static ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " " + ErrorCode.NOT_FOUND.getDescription());
    }

    /** 409 — caller supplies the full message. */
    public static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    /** 409 — "<subject> Record already exists". */
    public static ResponseStatusException alreadyExists(String subject) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                subject + " " + ErrorCode.ALREADY_EXISTS.getDescription());
    }

    /** 409 — "<subject> Code already exists". */
    public static ResponseStatusException codeExists(String subject) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                subject + " " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
    }

    /** 400 — caller supplies the full message. */
    public static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    /** Generic mapping from an {@link ErrorCode} plus a custom message. */
    public static ResponseStatusException of(ErrorCode code, String message) {
        return new ResponseStatusException(code.getHttpStatus(), message);
    }
}
