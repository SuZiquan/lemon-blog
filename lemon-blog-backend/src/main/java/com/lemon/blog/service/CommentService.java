package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lemon.blog.dao.CommentMapper;
import com.lemon.blog.dto.*;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.Comment;
import com.lemon.blog.model.enums.CommentStatus;
import com.lemon.blog.utils.AuthUtils;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ArticleService articleService;

    private final CommentMapper commentMapper;

    private final UserService userService;

    private final AuthUtils authUtils;

    @Cacheable(cacheNames = "comment_count")
    public int count() {
        return commentMapper.selectCount(null);
    }

    public int count(CommentDto criteria) {
        return commentMapper.selectCount(Wrappers.<Comment>query()
                .eq(criteria.getStatus() != null, "status", criteria.getStatus()));
    }

    @CacheEvict(cacheNames = "comment_count", allEntries = true)
    public void createComment(CommentDto commentDto) {
        Comment comment = BeanUtils.convertTo(commentDto, Comment.class);
        comment.setUserId(commentDto.getUser().getId());
        ArticleDto article = commentDto.getArticle();
        if (article == null || article.getId() == null) {
            throw new ApiException(ResultCode.INVALID_PARAMS);
        }
        comment.setArticleId(article.getId());
        UserDto replyToUser = commentDto.getReplyToUser();
        if (replyToUser != null && replyToUser.getId() != null) {
            comment.setReplyToUserId(replyToUser.getId());
        }
        comment.setStatus(CommentStatus.UNAUDITED.getValue());
        commentMapper.insert(comment);
    }

    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    public void auditComment(Long id, Integer status) {
        if (status == null || CommentStatus.fromValue(status) == null) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "无效的状态");
        }
        commentMapper.updateStatusById(id, status);
    }

    @CacheEvict(cacheNames = "comment_count", allEntries = true)
    public void deleteCommentById(Long id) {
        commentMapper.deleteById(id);
        commentMapper.delete(Wrappers.<Comment>query().eq("parent_id", id));
    }

    public PageInfoDto<CommentDto> pageComment(Integer pageNum, Integer pageSize, CommentDto criteria) {

        QueryWrapper<Comment> queryWrapper = Wrappers.query();
        if (criteria != null) {
            queryWrapper
                    .eq(criteria.getId() != null, "id", criteria.getId())
                    .eq(criteria.getArticle() != null && criteria.getArticle().getId() != null, "article_id", criteria.getId())
                    .eq(criteria.getStatus() != null, "status", criteria.getStatus());
        }
        queryWrapper.orderByDesc("created_at");

        Page<Comment> commentPage = commentMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        int total = (int) commentPage.getTotal();
        List<Comment> comments = commentPage.getRecords();

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    CommentDto commentDto = BeanUtils.convertTo(comment, CommentDto.class);
                    commentDto.setUser(userService.getUserById(comment.getUserId()));
                    if (comment.getReplyToUserId() != null) {
                        commentDto.setReplyToUser(userService.getUserById(comment.getReplyToUserId()));
                    }
                    ArticleDto article = articleService.getArticle(comment.getArticleId());
                    ArticleDto articleBasicInfo = new ArticleDto();
                    articleBasicInfo.setId(article.getId());
                    articleBasicInfo.setTitle(article.getTitle());
                    commentDto.setArticle(articleBasicInfo);
                    return commentDto;
                })
                .collect(Collectors.toList());
        return new PageInfoDto<>(total, pageNum, pageSize, rewriteUnauditedComments(commentDtos));
    }

    public PageInfoDto<CommentDto> pageArticleComment(Long articleId, Integer pageNum, Integer pageSize) {
        Page<Comment> commentPage = commentMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<Comment>query()
                .eq("article_id", articleId)
                .isNull("parent_id")
                .orderByDesc("created_at"));

        int total = (int) commentPage.getTotal();
        List<Comment> comments = commentPage.getRecords();

        List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> subCommentsGroupByParentId = commentIds.isEmpty() ? Collections.emptyMap()
                : commentMapper.selectList(Wrappers.<Comment>query().
                in("parent_id", commentIds))
                .stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    CommentDto commentDto = BeanUtils.convertTo(comment, CommentDto.class);
                    commentDto.setUser(userService.getUserById(comment.getUserId()));
                    if (comment.getReplyToUserId() != null) {
                        commentDto.setReplyToUser(userService.getUserById(comment.getReplyToUserId()));
                    }
                    if (subCommentsGroupByParentId.containsKey(comment.getId())) {
                        List<CommentDto> subCommentDtos =
                                subCommentsGroupByParentId.get(comment.getId()).stream().map(subComment -> {
                                    CommentDto subCommentDto = BeanUtils.convertTo(subComment, CommentDto.class);
                                    subCommentDto.setUser(userService.getUserById(subComment.getUserId()));
                                    if (subComment.getReplyToUserId() != null) {
                                        subCommentDto.setReplyToUser(userService.getUserById(subComment.getReplyToUserId()));
                                    }
                                    return subCommentDto;
                                }).collect(Collectors.toList());
                        commentDto.setSubComments(subCommentDtos);
                    }
                    return commentDto;
                })
                .collect(Collectors.toList());
        return new PageInfoDto<>(total, pageNum, pageSize, rewriteUnauditedComments(commentDtos));
    }


    public List<CommentDto> rewriteUnauditedComments(List<CommentDto> commentDtos) {
        if (authUtils.currentUserIsAdmin()) {
            return commentDtos;
        }
        AuthUser currentUser = authUtils.getCurrentUser();
        return rewriteUnauditedComments(currentUser == null ? null : currentUser.getId(),
                commentDtos);

    }

    public List<CommentDto> rewriteUnauditedComments(Long currentUserId, List<CommentDto> commentDtos) {
        return commentDtos.stream().peek(commentDto -> {
            if (CommentStatus.fromValue(commentDto.getStatus()) != CommentStatus.AUDITED
                    && !commentDto.getUser().getId().equals(currentUserId)) {
                commentDto.setContent(null);
            }
            if (commentDto.getSubComments() != null) {
                commentDto.setSubComments(rewriteUnauditedComments(currentUserId, commentDto.getSubComments()));
            }
        }).collect(Collectors.toList());
    }

}