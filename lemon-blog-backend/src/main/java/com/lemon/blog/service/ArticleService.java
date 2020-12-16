package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lemon.blog.dao.ArticleCategoryMapper;
import com.lemon.blog.dao.ArticleMapper;
import com.lemon.blog.dao.ArticleTagMapper;
import com.lemon.blog.dto.ArticleDto;
import com.lemon.blog.dto.CategoryDto;
import com.lemon.blog.dto.PageInfoDto;
import com.lemon.blog.dto.TagDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.*;
import com.lemon.blog.model.enums.ArticleStatus;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleMapper articleMapper;

    private final ArticleCategoryMapper articleCategoryMapper;

    private final ArticleTagMapper articleTagMapper;

    private final CategoryService categoryService;

    private final TagService tagService;

    @Cacheable(cacheNames = "article_count")
    public int count() {
        return articleMapper.selectCount(null);
    }

    @CacheEvict(cacheNames = {"article_paging", "article_all", "article_count", "categories", "tags"}, allEntries = true)
    @Transactional(rollbackFor = {Exception.class})
    public void createArticle(ArticleDto articleDto) {
        Article article = BeanUtils.convertTo(articleDto, Article.class);
        articleMapper.insert(article);
        Long articleId = article.getId();
        insertCategoriesAndTags(articleId, getCategoryIds(articleDto, true), getTagIds(articleDto, true));
    }

    private List<Long> getCategoryIds(ArticleDto articleDto, boolean createIfNotExist) {
        if (articleDto == null || articleDto.getCategories() == null) {
            return Collections.emptyList();
        }
        Map<String, Category> categoryByName = categoryService.getCategoriesByIds(null)
                .stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        List<Long> categoryIds = new ArrayList<>();
        for (CategoryDto categoryDto : articleDto.getCategories()) {
            Long id = categoryDto.getId();
            if (id != null) {
                categoryIds.add(id);
            } else {
                String name = categoryDto.getName();
                if (categoryByName.containsKey(name)) {
                    categoryIds.add(categoryByName.get(name).getId());
                } else if (createIfNotExist && name != null && !name.isEmpty()) {
                    categoryIds.add(categoryService.createCategory(categoryDto));
                }
            }
        }
        return categoryIds;
    }

    private List<Long> getTagIds(ArticleDto articleDto, boolean createIfNotExist) {
        if (articleDto == null || articleDto.getTags() == null) {
            return Collections.emptyList();
        }
        Map<String, Tag> tagByName = tagService.getTagsByIds(null)
                .stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        List<Long> tagIds = new ArrayList<>();
        for (TagDto tagDto : articleDto.getTags()) {
            Long id = tagDto.getId();
            if (id != null) {
                tagIds.add(id);
            } else {
                String name = tagDto.getName();
                if (tagByName.containsKey(name)) {
                    tagIds.add(tagByName.get(name).getId());
                } else if (createIfNotExist && name != null && !name.isEmpty()) {
                    tagIds.add(tagService.createTag(tagDto));
                }
            }
        }
        return tagIds;

    }

    private void insertCategoriesAndTags(Long articleId, List<Long> categoryIds, List<Long> tagIds) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "至少选择一个分类");
        }

        List<ArticleCategory> articleCategoryRelations = categoryIds.stream()
                .map(categoryId -> new ArticleCategory(articleId, categoryId))
                .collect(Collectors.toList());
        articleCategoryMapper.insertBatch(articleCategoryRelations);

        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTagRelations = tagIds.stream()
                    .map(tagId -> new ArticleTag(articleId, tagId))
                    .collect(Collectors.toList());
            articleTagMapper.insertBatch(articleTagRelations);
        }
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "article", key = "#id"),
                    @CacheEvict(cacheNames = {"article_paging", "article_all", "article_count", "categories", "tags"}, allEntries = true)
            }
    )
    @Transactional(rollbackFor = {Exception.class})
    public void deleteArticleById(Long id) {
        articleMapper.deleteById(id);
        deleteCategoriesAndTags(id);
    }

    private void deleteCategoriesAndTags(Long articleId) {
        articleCategoryMapper.delete(Wrappers.<ArticleCategory>query().eq("article_id", articleId));
        articleTagMapper.delete(Wrappers.<ArticleTag>query().eq("article_id", articleId));
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "article", key = "#articleDto.id"),
                    @CacheEvict(cacheNames = {"article_paging", "article_all", "article_count", "categories", "tags"}, allEntries = true)
            }
    )
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticle(ArticleDto articleDto) {
        Long articleId = articleDto.getId();
        Article article = BeanUtils.convertTo(articleDto, Article.class);
        articleMapper.updateById(article);
        deleteCategoriesAndTags(articleId);
        insertCategoriesAndTags(articleId, getCategoryIds(articleDto, true), getTagIds(articleDto, true));
    }

    @Cacheable(cacheNames = "article", key = "#id", unless = "#result == null")
    public ArticleDto getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new ApiException(ResultCode.NOT_FOUND, "文章不存在");
        }
        ArticleDto articleDto = BeanUtils.convertTo(article, ArticleDto.class);

        List<Long> articleIds = Collections.singletonList(id);
        List<CategoryDto> categoryDtos = getCategoriesGroupByArticleId(articleIds)
                .getOrDefault(id, Collections.emptyList())
                .stream()
                .map(category -> BeanUtils.convertTo(category, CategoryDto.class))
                .collect(Collectors.toList());
        articleDto.setCategories(categoryDtos);

        List<TagDto> tagDtos = getTagsGroupByArticleId(articleIds)
                .getOrDefault(id, Collections.emptyList())
                .stream()
                .map(tag -> BeanUtils.convertTo(tag, TagDto.class))
                .collect(Collectors.toList());
        articleDto.setTags(tagDtos);

        Article previous = articleMapper.selectOne(Wrappers.<Article>query()
                .gt("id", id)
                .eq("status", ArticleStatus.PUBLIC.getValue())
                .orderByAsc("id")
                .last(" limit 1")
                .select("id", "title"));
        Article next = articleMapper.selectOne(Wrappers.<Article>query()
                .lt("id", id)
                .eq("status", ArticleStatus.PUBLIC.getValue())
                .orderByDesc("id")
                .last(" limit 1")
                .select("id", "title"));

        if (previous != null) {
            articleDto.setPrevious(BeanUtils.convertTo(previous, ArticleDto.class));
        }
        if (next != null) {
            articleDto.setNext(BeanUtils.convertTo(next, ArticleDto.class));
        }
        return articleDto;
    }

    private Map<Long, List<Category>> getCategoriesGroupByArticleId(List<Long> articleIds) {
        List<ArticleCategory> articleCategoryRelations = articleCategoryMapper.selectList(
                Wrappers.<ArticleCategory>query()
                        .in(!articleIds.isEmpty(), "article_id", articleIds)
                        .select("article_id", "category_id"));

        List<Long> categoryIds = articleCategoryRelations
                .stream()
                .map(ArticleCategory::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Category> categoryById = categoryService.getCategoriesByIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        return articleCategoryRelations.stream().collect(
                Collectors.groupingBy(
                        ArticleCategory::getArticleId,
                        Collectors.mapping(
                                articleCategory -> categoryById.get(articleCategory.getCategoryId()),
                                Collectors.toList()
                        )
                )
        );
    }

    private Map<Long, List<Tag>> getTagsGroupByArticleId(List<Long> articleIds) {
        List<ArticleTag> articleTagRelations = articleTagMapper.selectList(
                Wrappers.<ArticleTag>query()
                        .in(!articleIds.isEmpty(), "article_id", articleIds)
                        .select("article_id", "tag_id"));

        List<Long> tagIds = articleTagRelations
                .stream()
                .map(ArticleTag::getTagId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Tag> tagById = tagService.getTagsByIds(tagIds)
                .stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));

        return articleTagRelations.stream().collect(
                Collectors.groupingBy(
                        ArticleTag::getArticleId,
                        Collectors.mapping(
                                articleTag -> tagById.get(articleTag.getTagId()),
                                Collectors.toList()
                        )
                )
        );
    }


    @Cacheable(cacheNames = "article_paging")
    public PageInfoDto<ArticleDto> pageArticle(Integer pageNum, Integer pageSize, ArticleDto criteria) {
        List<Long> categoryIds = getCategoryIds(criteria, false);
        List<Long> tagIds = getTagIds(criteria, false);

        Set<Long> filteredIds = null;

        if (!categoryIds.isEmpty()) {
            Set<Long> filteredIdsByCategory = articleCategoryMapper.selectList(Wrappers.<ArticleCategory>query()
                    .in("category_id", categoryIds)
                    .select("article_id"))
                    .stream()
                    .map(ArticleCategory::getArticleId)
                    .collect(Collectors.toSet());
            if (filteredIdsByCategory.isEmpty()) {
                return new PageInfoDto<>(0, pageNum, pageSize, Collections.emptyList());
            }
            filteredIds = filteredIdsByCategory;
        }

        if (!tagIds.isEmpty()) {
            Set<Long> filteredIdsByTag = articleTagMapper.selectList(Wrappers.<ArticleTag>query()
                    .in("tag_id", tagIds)
                    .select("article_id"))
                    .stream()
                    .map(ArticleTag::getArticleId)
                    .collect(Collectors.toSet());
            if (filteredIdsByTag.isEmpty()) {
                return new PageInfoDto<>(0, pageNum, pageSize, Collections.emptyList());
            }
            if (filteredIds == null) {
                filteredIds = filteredIdsByTag;
            } else {
                filteredIds.retainAll(filteredIdsByTag);
            }
        }

        QueryWrapper<Article> queryWrapper = Wrappers.query();
        if (criteria != null) {
            queryWrapper
                    .like(StringUtils.isNotEmpty(criteria.getTitle()), "title", criteria.getTitle())
                    .like(StringUtils.isNotEmpty(criteria.getContent()), "content", criteria.getContent())
                    .like(StringUtils.isNotEmpty(criteria.getDescription()), "description", criteria.getDescription())
                    .eq(criteria.getStatus() != null, "status", criteria.getStatus())
                    .in(filteredIds != null && !filteredIds.isEmpty(), "id", filteredIds);
        }

        queryWrapper.orderByDesc("created_at")
                .select(Article.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("content"));
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        return new PageInfoDto<>((int) articlePage.getTotal(), pageNum, pageSize, toArticleDtos(articlePage.getRecords()));
    }

    private List<ArticleDto> toArticleDtos(List<Article> articles) {

        List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());

        Map<Long, List<Category>> categoriesGroupByArticleId = getCategoriesGroupByArticleId(articleIds);
        Map<Long, List<Tag>> tagsGroupByArticleId = getTagsGroupByArticleId(articleIds);

        return articles.stream().map(article -> {
            Long articleId = article.getId();
            ArticleDto articleDto = BeanUtils.convertTo(article, ArticleDto.class);
            articleDto.setCategories(categoriesGroupByArticleId
                    .getOrDefault(articleId, Collections.emptyList())
                    .stream()
                    .map(category -> BeanUtils.convertTo(category, CategoryDto.class))
                    .collect(Collectors.toList()));
            articleDto.setTags(tagsGroupByArticleId
                    .getOrDefault(articleId, Collections.emptyList())
                    .stream()
                    .map(tag -> BeanUtils.convertTo(tag, TagDto.class))
                    .collect(Collectors.toList()));
            return articleDto;
        }).collect(Collectors.toList());

    }

    public PageInfoDto<ArticleDto> searchArticle(Integer pageNum, Integer pageSize, String queryString, ArticleStatus articleStatus) {
        if (queryString == null || queryString.isEmpty()) {
            return new PageInfoDto<>(0, pageNum, pageSize, Collections.emptyList());
        }
        QueryWrapper<Article> queryWrapper = Wrappers.<Article>query()
                .eq(articleStatus != null, "status", articleStatus.getValue())
                .and(wrapper ->
                        wrapper.like("title", queryString)
                                .or()
                                .like("content", queryString)
                                .or()
                                .like("description", queryString))
                .orderByDesc("created_at")
                .select(Article.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("content"));
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        return new PageInfoDto<>((int) articlePage.getTotal(), pageNum, pageSize, toArticleDtos(articlePage.getRecords()));
    }


    @Cacheable(cacheNames = "article_all")
    public List<ArticleDto> getAllArticles(ArticleDto articleDto) {
        return articleMapper.selectList(Wrappers.<Article>query()
                .eq(articleDto.getStatus() != null, "status", articleDto.getStatus())
                .orderByDesc("created_at")
                .select("id", "title", "created_at"))
                .stream()
                .map(article -> BeanUtils.convertTo(article, ArticleDto.class))
                .collect(Collectors.toList());

    }
}