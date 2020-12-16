package com.lemon.blog.utils;

import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.dto.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;

    private SecretKey secretKey;

    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey()));
    }

    public String createToken(AuthUser authUser) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + jwtConfig.getExpirationInSeconds() * 1000);

        String jwtToken = Jwts.builder()
                .setHeaderParam("type", jwtConfig.getTokenType())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .claim("socialSource", authUser.getSocialSource())
                .claim("username", authUser.getUsername())
                .claim("avatarUrl", authUser.getAvatarUrl())
                .claim("role", authUser.getRole())
                .setIssuer("lemon-blog")
                .setIssuedAt(createdDate)
                .setSubject(authUser.getId().toString())
                .setExpiration(expirationDate)
                .compact();
        return jwtConfig.getTokenPrefix() + jwtToken;
    }

    public AuthUser getAuthUserByToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        AuthUser authUser = new AuthUser();
        authUser.setId(Long.parseLong(claims.getSubject()));
        authUser.setSocialSource(claims.get("socialSource", String.class));
        authUser.setUsername(claims.get("username", String.class));
        authUser.setAvatarUrl(claims.get("avatarUrl", String.class));
        authUser.setRole(claims.get("role", String.class));

        return authUser;
    }

}
