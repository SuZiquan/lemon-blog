package com.lemon.blog.dto;

import com.lemon.blog.exception.ResultCode;
import lombok.Data;

@Data
public class Result<T> {

    private int code;

    private String message;

    private T data;

    public Result(T data) {
        this(ResultCode.SUCCESS, data);
    }


    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(ResultCode resultCode, String message) {
        this.code = resultCode.getCode();
        this.message = message;
    }

    public Result(ResultCode resultCode, String message, T data) {
        this.code = resultCode.getCode();
        this.message = message;
        this.data = data;
    }

    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

}