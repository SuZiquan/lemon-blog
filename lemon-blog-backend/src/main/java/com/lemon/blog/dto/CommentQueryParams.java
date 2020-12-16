package com.lemon.blog.dto;

import lombok.Data;

@Data
public class CommentQueryParams {

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;

}
