package com.lemon.blog.controller;


import com.lemon.blog.dto.ConfigDto;
import com.lemon.blog.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/configs")
public class ConfigController {

    private final ConfigService configService;

    @PutMapping
    public void insertOrUpdateConfig(@RequestBody ConfigDto configDto) {
        configService.insertOrUpdateConfig(configDto);
    }

    @GetMapping
    public List<ConfigDto> getConfigs() {
        return configService.getConfigs();
    }

}
