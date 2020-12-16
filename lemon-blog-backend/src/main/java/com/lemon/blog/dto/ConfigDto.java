package com.lemon.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigDto {

    private Long id;

    private String name;

    private String value;

}