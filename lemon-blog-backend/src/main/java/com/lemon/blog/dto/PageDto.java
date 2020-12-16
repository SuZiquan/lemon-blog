package com.lemon.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PageDto {

    private Long id;

    private String name;

    private String icon;

    private String link;

    private String content;

    private Integer status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="GMT+8")
    private LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="GMT+8")
    private LocalDateTime updatedAt;

}

