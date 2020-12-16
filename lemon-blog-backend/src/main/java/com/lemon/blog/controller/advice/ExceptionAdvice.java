package com.lemon.blog.controller.advice;

import com.lemon.blog.dto.Result;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ApiException.class)
    public Result<String> apiExceptionHandler(ApiException e, HttpServletResponse response) {
        ResultCode resultCode = e.getResultCode();
        response.setStatus(resultCode.getHttpStatus().value());
        return new Result<>(resultCode, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<String> noHandlerFoundExceptionHandler(NoHandlerFoundException e, HttpServletResponse response) {
        ResultCode resultCode = ResultCode.NOT_FOUND;
        response.setStatus(resultCode.getHttpStatus().value());
        return new Result<>(resultCode, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception e, HttpServletResponse response) {
        ResultCode resultCode = ResultCode.SERVER_ERROR;
        response.setStatus(resultCode.getHttpStatus().value());
        e.printStackTrace();
        return new Result<>(resultCode, e.getMessage());
    }
}