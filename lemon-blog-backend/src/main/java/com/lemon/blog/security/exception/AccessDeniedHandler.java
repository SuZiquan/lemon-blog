package com.lemon.blog.security.exception;

import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private final AuthUtils authUtils;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        authUtils.writeAuthFailResponse(response, ResultCode.FORBIDDEN, accessDeniedException);
    }
}
