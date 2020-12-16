package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lemon.blog.dao.ConfigMapper;
import com.lemon.blog.dto.ConfigDto;
import com.lemon.blog.model.Config;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigMapper configMapper;

    @CacheEvict(cacheNames = "config_all", allEntries = true)
    public void insertOrUpdateConfig(ConfigDto configDto) {
        configMapper.insertOrUpdate(configDto.getName(), configDto.getValue());
    }

    @Cacheable(cacheNames = "config_all")
    public List<ConfigDto> getConfigs() {
        return configMapper.selectList(null)
                .stream()
                .map(config -> BeanUtils.convertTo(config, ConfigDto.class))
                .collect(Collectors.toList());
    }

}
