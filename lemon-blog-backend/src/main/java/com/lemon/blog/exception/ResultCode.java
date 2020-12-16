package com.lemon.blog.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    SUCCESS(HttpStatus.OK, 200000, "success"),
    INVALID_PARAMS(HttpStatus.BAD_REQUEST, 400000, "invalid params"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401000, "unauthorized"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404000, "resource not found"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403000, "forbidden"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500000, "server error");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}