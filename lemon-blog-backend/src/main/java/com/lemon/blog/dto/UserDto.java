package com.lemon.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long id;

    private String username;

    private String avatarUrl;

    private String socialSource;

    private String email;

    private LocalDateTime lastLogin;

}
