package com.lemon.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.blog.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("update user set username = #{username} where id = #{userId}")
    Integer updateUsernameById(Long userId, String username);

    @Update("update user set password = #{password} where id = #{userId}")
    Integer updatePasswordById(Long userId, String password);
}
