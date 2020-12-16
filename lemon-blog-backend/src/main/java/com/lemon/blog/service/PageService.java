package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lemon.blog.dao.PageMapper;
import com.lemon.blog.dto.PageDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.Page;
import com.lemon.blog.model.enums.PageStatus;
import com.lemon.blog.utils.AuthUtils;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageMapper pageMapper;

    private final AuthUtils authUtils;

    @CacheEvict(cacheNames = "pages", allEntries = true)
    public void createPage(PageDto pageDto) {
        pageMapper.insert(BeanUtils.convertTo(pageDto, Page.class));
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "page", key = "#id"),
                    @CacheEvict(cacheNames = "pages", allEntries = true)
            }
    )
    public void deletePageById(Long id) {
        pageMapper.deleteById(id);
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "page", key = "#pageDto.id"),
                    @CacheEvict(cacheNames = "pages", allEntries = true)
            }
    )
    public void updatePage(PageDto pageDto) {
        pageMapper.updateById(BeanUtils.convertTo(pageDto, Page.class));
    }

    @Cacheable(cacheNames = "page", key = "#id", unless = "#result == null")
    public PageDto getPage(Long id) {
        Page page = pageMapper.selectById(id);
        if (page == null) {
            throw new ApiException(ResultCode.NOT_FOUND, "页面不存在");
        }
        if (PageStatus.fromValue(page.getStatus()) != PageStatus.PUBLIC && !authUtils.currentUserIsAdmin()) {
            throw new ApiException(ResultCode.FORBIDDEN);
        }
        return BeanUtils.convertTo(page, PageDto.class);
    }

    @Cacheable(cacheNames = "pages")
    public List<PageDto> getPages(PageDto criteria) {
        return pageMapper.selectList(Wrappers.<Page>query()
                .eq(criteria.getStatus() != null, "status", criteria.getStatus())
                .select(Page.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("content")))
                .stream()
                .map(page -> BeanUtils.convertTo(page, PageDto.class))
                .collect(Collectors.toList());
    }
}
