package com.lemon.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDto {

    private Long id;

    private String title;

    private String description;

    private String content;

    private Integer status;

    private List<CategoryDto> categories;

    private List<TagDto> tags;

    private ArticleDto previous;

    private ArticleDto next;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

