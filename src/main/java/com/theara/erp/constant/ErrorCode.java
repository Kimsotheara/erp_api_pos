package com.theara.erp.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SUCCESS("0000", "Success", HttpStatus.OK, true),
    CREATED("0000", "Success", HttpStatus.CREATED, true),
    NOT_FOUND("0001", "Resource not found", HttpStatus.NOT_FOUND, false),
    METHOD_NOT_ALLOWED("0002", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED, false),
    BAD_REQUEST("0003", "Bad request", HttpStatus.BAD_REQUEST, false),
    INTERNAL_SERVER_ERROR("0004", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, false),
    UNAUTHORIZED("0005", "Unauthorized", HttpStatus.UNAUTHORIZED, false),
    CONFLICT("0006", "Conflict", HttpStatus.CONFLICT, false),
    FORBIDDEN("0007", "Forbidden", HttpStatus.FORBIDDEN, false),
    ALREADY_EXISTS("0008", "Record already exists", HttpStatus.CONFLICT, false),
    CODE_ALREADY_EXISTS("0009", "Code already exists", HttpStatus.CONFLICT, false),
    SKU_ALREADY_EXISTS("0010", "SKU already exists", HttpStatus.CONFLICT, false),
    INSUFFICIENT_STOCK("0011", "Insufficient stock", HttpStatus.CONFLICT, false),
    UNSUPPORTED_MEDIA_TYPE("0014", "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE, false);

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;
    private final boolean result;

    ErrorCode(String code, String description, HttpStatus httpStatus, boolean result) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
        this.result = result;
    }
}
