package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lemon.blog.dao.ArticleCategoryMapper;
import com.lemon.blog.dao.CategoryMapper;
import com.lemon.blog.dto.CategoryDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.ArticleCategory;
import com.lemon.blog.model.Category;
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
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final ArticleCategoryMapper articleCategoryMapper;

    @Cacheable(cacheNames = "category_count")
    public int count() {
        return categoryMapper.selectCount(null);
    }

    @CacheEvict(cacheNames = {"categories", "category_count"}, allEntries = true)
    public Long createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.selectOne(
                Wrappers.<Category>query()
                        .eq("name", categoryDto.getName())
                        .select("id"));
        if (category != null) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "该分类名已存在");
        }
        category = BeanUtils.convertTo(categoryDto, Category.class);
        categoryMapper.insert(category);
        return category.getId();
    }

    @CacheEvict(cacheNames = {"categories", "category_count"}, allEntries = true)
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    public List<Category> getCategoriesByIds(List<Long> ids) {
        return categoryMapper.selectList(Wrappers.<Category>query()
                .in(ids != null && !ids.isEmpty(), "id", ids)
                .select("id", "name"));
    }

    @Cacheable(cacheNames = "categories")
    public List<CategoryDto> getCategoriesWithArticleCount() {
        Map<Long, Long> articleCountGroupByCategoryId = articleCategoryMapper.selectList(null)
                .stream()
                .collect(Collectors.groupingBy(
                        ArticleCategory::getCategoryId,
                        Collectors.counting()));

        return categoryMapper.selectList(null).stream().map(category -> {
            int articleCount = articleCountGroupByCategoryId.getOrDefault(category.getId(), 0L).intValue();
            CategoryDto categoryDto = BeanUtils.convertTo(category, CategoryDto.class);
            categoryDto.setArticleCount(articleCount);
            return categoryDto;
        }).collect(Collectors.toList());
    }

}
