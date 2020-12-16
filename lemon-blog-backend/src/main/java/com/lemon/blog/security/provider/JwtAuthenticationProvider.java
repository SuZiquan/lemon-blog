package com.lemon.blog.security.provider;

import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.dto.AuthUser;
import com.lemon.blog.security.token.JwtAuthToken;
import com.lemon.blog.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtConfig jwtConfig;

    private final JwtUtils jwtUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthToken jwtAuthToken = (JwtAuthToken) authentication;
        String jwtTokenStringWithPrefix = (String) jwtAuthToken.getCredentials();
        String jwtTokenString = jwtTokenStringWithPrefix.replace(jwtConfig.getTokenPrefix(), "");

        try {
            AuthUser authUser = jwtUtils.getAuthUserByToken(jwtTokenString);
            return new JwtAuthToken(authUser);
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid jwt token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthToken.class.isAssignableFrom(authentication));
    }
}