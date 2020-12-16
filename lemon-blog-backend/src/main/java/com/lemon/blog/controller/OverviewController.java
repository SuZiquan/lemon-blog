package com.lemon.blog.controller;

import com.google.common.collect.ImmutableMap;
import com.lemon.blog.dto.PageDto;
import com.lemon.blog.model.enums.PageStatus;
import com.lemon.blog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@ApiController
@RestController
@RequestMapping("/overview")
@RequiredArgsConstructor
public class OverviewController {

    private final ArticleService articleService;

    private final CommentService commentService;

    private final CategoryService categoryService;

    private final TagService tagService;

    private final PageService pageService;

    private final ConfigService configService;

    @GetMapping()
    public Map<String, Object> overview() {
        PageDto pageCriteria = new PageDto();
        pageCriteria.setStatus(PageStatus.PUBLIC.getValue());
        return ImmutableMap.of(
                "resourceCount", ImmutableMap.of(
                        "article", articleService.count(),
                        "comment", commentService.count(),
                        "category", categoryService.count(),
                        "tag", tagService.count()),
                "tags", tagService.getTagsWithArticleCount(),
                "categories", categoryService.getCategoriesWithArticleCount(),
                "pages", pageService.getPages(pageCriteria),
                "configs", configService.getConfigs()
        );
    }

}
