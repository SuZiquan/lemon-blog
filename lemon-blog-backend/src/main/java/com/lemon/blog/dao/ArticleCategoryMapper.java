package com.lemon.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.blog.model.ArticleCategory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {

    @Insert({
            "<script>",
            "insert into article_category (article_id, category_id)",
            "values ",
            "<foreach  collection='articleCategoryRelations' item='relation' separator=','>",
            "( #{relation.articleId}, #{relation.categoryId})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("articleCategoryRelations") List<ArticleCategory> articleCategoryRelations);

}