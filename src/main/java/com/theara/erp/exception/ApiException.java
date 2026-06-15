package com.theara.erp.exception;

import com.theara.erp.constant.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class ApiException {
    private ApiException() {
    }

    public static ResponseStatusException notFound(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " " + ErrorCode.NOT_FOUND.getDescription());
    }

    public static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    public static ResponseStatusException alreadyExists(String subject) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                subject + " " + ErrorCode.ALREADY_EXISTS.getDescription());
    }

    public static ResponseStatusException codeExists(String subject) {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                subject + " " + ErrorCode.CODE_ALREADY_EXISTS.getDescription());
    }

    public static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseStatusException of(ErrorCode code, String message) {
        return new ResponseStatusException(code.getHttpStatus(), message);
    }
}
