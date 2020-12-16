package com.lemon.blog.dto;

import lombok.Data;

@Data
public class UserQueryParams {

    private String username;

    private String socialSource;

    private String email;

    private Integer pageNum;

    private Integer pageSize;

}