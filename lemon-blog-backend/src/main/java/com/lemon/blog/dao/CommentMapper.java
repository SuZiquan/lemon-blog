package com.lemon.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.blog.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Update("update comment set status = #{status} where id = #{commentId}")
    Integer updateStatusById(Long commentId, Integer status);

}
