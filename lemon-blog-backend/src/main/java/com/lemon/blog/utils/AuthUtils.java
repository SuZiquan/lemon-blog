package com.lemon.blog.utils;

import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.dto.AuthUser;
import com.lemon.blog.dto.Result;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final JwtUtils jwtUtils;

    private final JwtConfig jwtConfig;

    public void writeAuthSuccessResponse(HttpServletResponse response, Authentication authResult) throws IOException {
        AuthUser authUser = (AuthUser) authResult.getPrincipal();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(jwtConfig.getTokenHeader(), jwtUtils.createToken(authUser));
        writeResponse(response, ResultCode.SUCCESS, ResultCode.SUCCESS.getMessage(), authUser);
    }

    public void writeAuthFailResponse(HttpServletResponse response, ResultCode resultCode, Exception exception) throws IOException {
        writeResponse(response, resultCode, exception.getMessage(), null);
    }

    private void writeResponse(HttpServletResponse response, ResultCode resultCode, String message, Object data) throws IOException {
        response.setStatus(resultCode.getHttpStatus().value());

        Result<?> result = new Result<>(resultCode, message, data);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonUtils.toJson(result));
        response.getWriter().flush();
    }


    public AuthUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof AuthUser) {
           return (AuthUser) authentication.getPrincipal();
        }
        return null;
    }

    public boolean currentUserIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof AuthUser) {
            AuthUser authUser = (AuthUser) authentication.getPrincipal();
            return authUser.hasRole(UserRole.ADMIN);
        }
        return false;
    }
}
