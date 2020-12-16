package com.lemon.blog.config;

import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.TimeUnit;

@Data
public class JwtConfig {

    private String secretKey;

    private String tokenHeader = HttpHeaders.AUTHORIZATION;

    private String tokenPrefix = "Bearer ";

    private String tokenType = "JWT";

    private Long expirationInSeconds = TimeUnit.HOURS.toSeconds(1);

}
