package com.lemon.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private Long id;

    private ArticleDto article;

    private UserDto user;

    private UserDto replyToUser;

    private String content;

    private Integer status;

    private List<CommentDto> subComments;

    private Long parentId;

    private LocalDateTime createdAt;

}

