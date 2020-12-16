package com.lemon.blog.controller;

import com.lemon.blog.dto.*;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.Comment;
import com.lemon.blog.model.enums.UserRole;
import com.lemon.blog.service.CommentService;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    private final AuthUtils authUtils;

    @PostMapping
    public void createComment(@RequestBody CommentDto commentDto) {
        if (StringUtils.isEmpty(commentDto.getContent())) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "评论内容不能为空");
        }
        Long currentUserId = authUtils.getCurrentUser().getId();
        UserDto userDto = new UserDto();
        userDto.setId(currentUserId);
        commentDto.setUser(userDto);
        commentService.createComment(commentDto);
    }

    @GetMapping("/count")
    public Integer count(CommentQueryParams queryParams) {
        CommentDto criteria = new CommentDto();
        if (queryParams != null) {
            criteria.setStatus(queryParams.getStatus());
        }
        return commentService.count(criteria);
    }

    @PostMapping("/{id}/audit/{status}")
    public void auditComment(@PathVariable Long id, @PathVariable Integer status) {
        commentService.auditComment(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        AuthUser currentUser = authUtils.getCurrentUser();
        boolean isAdmin = currentUser.hasRole(UserRole.ADMIN);
        if (!isAdmin) {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return;
            }
            if (!currentUser.getId().equals(comment.getUserId())) {
                throw new ApiException(ResultCode.FORBIDDEN);
            }
        }
        commentService.deleteCommentById(id);
    }

    @GetMapping
    public PageInfoDto<CommentDto> pageComment(CommentQueryParams queryParams) {

        int pageNum = PageInfoDto.DEFAULT_PAGE_NUM;
        int pageSize = PageInfoDto.DEFAULT_PAGE_SIZE;

        CommentDto criteria = new CommentDto();
        if (queryParams != null) {
            criteria.setStatus(queryParams.getStatus());

            if (queryParams.getPageNum() != null) {
                pageNum = queryParams.getPageNum();
            }
            if (queryParams.getPageSize() != null) {
                pageSize = queryParams.getPageSize();
            }
        }
        return commentService.pageComment(pageNum, pageSize, criteria);
    }

}
