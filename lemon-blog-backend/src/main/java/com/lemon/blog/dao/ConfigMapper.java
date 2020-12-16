package com.lemon.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.blog.model.Config;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    @Insert("insert into config (name, value) values(#{name}, #{value}) on duplicate key update value = values(value)")
    void insertOrUpdate(String name, String value);

}
