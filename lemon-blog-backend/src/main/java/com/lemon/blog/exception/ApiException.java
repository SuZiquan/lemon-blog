package com.lemon.blog.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ResultCode resultCode;
    private final String message;

    public ApiException(Exception e) {
        super(e);
        this.resultCode = ResultCode.SERVER_ERROR;
        this.message = resultCode.getMessage();
    }

    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    public ApiException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.message = message;
    }

}