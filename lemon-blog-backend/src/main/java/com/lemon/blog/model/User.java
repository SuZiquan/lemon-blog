package com.lemon.blog.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;

    private String socialSource;

    private String socialUserId;

    private String username;

    private String avatarUrl;

    private String email;

    private String password;

    private String role;

    private LocalDateTime lastLogin;

}