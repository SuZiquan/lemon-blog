package com.lemon.blog.controller;


import com.lemon.blog.dto.TagDto;
import com.lemon.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagDto> getTags () {
        return tagService.getTagsWithArticleCount();
    }

    @PostMapping
    public void createTag(@RequestBody TagDto tagDto) {
        tagService.createTag(tagDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagService.deleteTagById(id);
    }
    
}
