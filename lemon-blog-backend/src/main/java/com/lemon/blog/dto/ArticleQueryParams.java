package com.lemon.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleQueryParams {

    private Long id;

    private String title;

    private String content;

    private List<Long> categoryIds;

    private List<Long> tagIds;

    private Integer pageNum;

    private Integer pageSize;

}
