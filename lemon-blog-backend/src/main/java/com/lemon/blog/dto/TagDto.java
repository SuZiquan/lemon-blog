package com.lemon.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagDto {

    private Long id;

    private String name;

    private Integer articleCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

