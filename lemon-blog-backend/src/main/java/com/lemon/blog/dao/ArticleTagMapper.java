package com.lemon.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.blog.model.ArticleTag;
import com.lemon.blog.model.ArticleTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    @Insert({
            "<script>",
            "insert into article_tag (article_id, tag_id)",
            "values ",
            "<foreach  collection='articleTagRelations' item='relation' separator=','>",
            "( #{relation.articleId}, #{relation.tagId})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("articleTagRelations") List<ArticleTag> articleTagRelations);
}