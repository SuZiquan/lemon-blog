package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lemon.blog.dao.ArticleTagMapper;
import com.lemon.blog.dao.TagMapper;
import com.lemon.blog.dto.TagDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.ArticleTag;
import com.lemon.blog.model.Tag;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Cacheable(cacheNames = "tag_count")
    public int count() {
        return tagMapper.selectCount(null);
    }

    @CacheEvict(cacheNames = {"tags", "tag_count"}, allEntries = true)
    public Long createTag(TagDto tagDto) {
        Tag tag = tagMapper.selectOne(
                Wrappers.<Tag>query()
                        .eq("name", tagDto.getName())
                        .select("id"));
        if (tag != null) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "该标签已存在");
        }
        tag = BeanUtils.convertTo(tagDto, Tag.class);
        tagMapper.insert(tag);
        return tag.getId();
    }

    @CacheEvict(cacheNames = {"tags", "tag_count"}, allEntries = true)
    public void deleteTagById(Long id) {
        tagMapper.deleteById(id);
    }

    public List<Tag> getTagsByIds(List<Long> ids) {
        return tagMapper.selectList(Wrappers.<Tag>query()
                .in(ids != null && !ids.isEmpty(), "id", ids)
                .select("id", "name"));
    }

    @Cacheable(cacheNames = "tags")
    public List<TagDto> getTagsWithArticleCount() {
        Map<Long, Long> articleCountGroupByTagId = articleTagMapper.selectList(null)
                .stream()
                .collect(Collectors.groupingBy(
                        ArticleTag::getTagId,
                        Collectors.counting()));

        return tagMapper.selectList(null).stream().map(tag -> {
            int articleCount = articleCountGroupByTagId.getOrDefault(tag.getId(), 0L).intValue();
            TagDto tagDto = BeanUtils.convertTo(tag, TagDto.class);
            tagDto.setArticleCount(articleCount);
            return tagDto;
        }).collect(Collectors.toList());
    }

}
