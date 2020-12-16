package com.lemon.blog.controller;


import com.lemon.blog.dto.PageDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.enums.PageStatus;
import com.lemon.blog.service.PageService;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/pages")
public class PageController {

    private final PageService pageService;

    private final AuthUtils authUtils;

    @GetMapping
    public List<PageDto> getPages() {
        PageDto criteria = new PageDto();
        if (!authUtils.currentUserIsAdmin()) {
            criteria.setStatus(PageStatus.PUBLIC.getValue());
        }
        return pageService.getPages(criteria);
    }

    @PostMapping
    public void createPage(@RequestBody PageDto pageDto) {
        pageService.createPage(pageDto);
    }

    @GetMapping("/{id}")
    public PageDto getPage(@PathVariable Long id) {
        PageDto pageDto = pageService.getPage(id);
        if (PageStatus.fromValue(pageDto.getStatus()) != PageStatus.PUBLIC
                && !authUtils.currentUserIsAdmin()) {
            throw new ApiException(ResultCode.FORBIDDEN);
        }
        return pageDto;
    }

    @PutMapping("/{id}")
    public void updatePage(@PathVariable Long id, @RequestBody PageDto pageDto) {
        pageDto.setId(id);
        pageService.updatePage(pageDto);
    }

    @DeleteMapping("/{id}")
    public void deletePage(@PathVariable Long id) {
        pageService.deletePageById(id);
    }

}
