package com.lemon.blog.controller;

import com.lemon.blog.dto.*;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.enums.ArticleStatus;
import com.lemon.blog.service.ArticleService;
import com.lemon.blog.service.CommentService;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    private final CommentService commentService;

    private final AuthUtils authUtils;

    @PostMapping
    public void createArticle(@RequestBody ArticleDto articleDto) {
        articleService.createArticle(articleDto);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticleById(id);
    }

    @PutMapping("/{id}")
    public void updateArticle(@PathVariable Long id, @RequestBody ArticleDto articleDto) {
        articleDto.setId(id);
        articleService.updateArticle(articleDto);
    }

    @GetMapping("/{id}")
    public ArticleDto getArticle(@PathVariable Long id) {
        ArticleDto articleDto = articleService.getArticle(id);
        if (ArticleStatus.fromValue(articleDto.getStatus()) != ArticleStatus.PUBLIC
                && !authUtils.currentUserIsAdmin()) {
            throw new ApiException(ResultCode.FORBIDDEN);
        }
        return articleDto;
    }

    @GetMapping("/all")
    public List<ArticleDto> getAllArticles() {
        ArticleDto criteria = new ArticleDto();
        if (!authUtils.currentUserIsAdmin()) {
            criteria.setStatus(ArticleStatus.PUBLIC.getValue());
        }
        return articleService.getAllArticles(criteria);
    }

    @GetMapping
    public PageInfoDto<ArticleDto> pageArticle(ArticleQueryParams queryParams) {
        int pageNum = PageInfoDto.DEFAULT_PAGE_NUM;
        int pageSize = PageInfoDto.DEFAULT_PAGE_SIZE;

        ArticleDto criteria = new ArticleDto();
        if (queryParams != null) {
            criteria.setId(queryParams.getId());
            criteria.setTitle(queryParams.getTitle());
            criteria.setContent(queryParams.getContent());

            if (queryParams.getCategoryIds() != null) {
                criteria.setCategories(queryParams.getCategoryIds().stream().map(categoryId -> {
                    CategoryDto categoryDto = new CategoryDto();
                    categoryDto.setId(categoryId);
                    return categoryDto;
                }).collect(Collectors.toList()));
            }

            if (queryParams.getTagIds() != null) {
                criteria.setTags(queryParams.getTagIds().stream().map(tagId -> {
                    TagDto tagDto = new TagDto();
                    tagDto.setId(tagId);
                    return tagDto;
                }).collect(Collectors.toList()));
            }

            if (queryParams.getPageNum() != null) {
                pageNum = queryParams.getPageNum();
            }
            if (queryParams.getPageSize() != null) {
                pageSize = queryParams.getPageSize();
            }
        }

        if (!authUtils.currentUserIsAdmin()) {
            criteria.setStatus(ArticleStatus.PUBLIC.getValue());
        }

        return articleService.pageArticle(pageNum, pageSize, criteria);
    }

    @GetMapping("/search")
    public PageInfoDto<ArticleDto> searchArticle(@RequestParam(required = false) Integer pageNum,
                                                 @RequestParam(required = false) Integer pageSize,
                                                 @RequestParam String queryString) {
        if (pageNum == null) {
            pageNum = PageInfoDto.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = PageInfoDto.DEFAULT_PAGE_SIZE;
        }

        ArticleStatus articleStatus = authUtils.currentUserIsAdmin() ? null : ArticleStatus.PUBLIC;
        return articleService.searchArticle(pageNum, pageSize, queryString, articleStatus);
    }

    @GetMapping("/{id}/comments")
    public PageInfoDto<CommentDto> pageArticleComments(
            @PathVariable Long id,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        if (pageNum == null) {
            pageNum = PageInfoDto.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null) {
            pageSize = PageInfoDto.DEFAULT_PAGE_SIZE;
        }


        return commentService.pageArticleComment(id, pageNum, pageSize);
    }


}
