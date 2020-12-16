package com.lemon.blog.security.filter;

import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.security.token.JwtAuthToken;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtConfig jwtConfig;

    private final AuthUtils authUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(jwtConfig.getTokenHeader());
        if (token == null || !token.startsWith(jwtConfig.getTokenPrefix())) {
            SecurityContextHolder.clearContext();
        } else {
            try {
                Authentication authResult = authenticationManager.authenticate(new JwtAuthToken(token));
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException authenticationException) {
                SecurityContextHolder.clearContext();
                authUtils.writeAuthFailResponse(response, ResultCode.UNAUTHORIZED, authenticationException);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
