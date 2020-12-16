package com.lemon.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JwtConfiguration {
    @Bean
    @ConfigurationProperties("jwt")
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

}
