package com.lemon.blog.security.exception;

import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final AuthUtils authUtils;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        authUtils.writeAuthFailResponse(response, ResultCode.UNAUTHORIZED, authException);
    }
}
