package com.lemon.blog.controller;

import com.lemon.blog.dto.PageInfoDto;
import com.lemon.blog.dto.UserDto;
import com.lemon.blog.dto.UserQueryParams;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        if (userDto == null) {
            throw new ApiException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return userDto;
    }

    @GetMapping
    public PageInfoDto<UserDto> pageUser(UserQueryParams queryParams) {

        int pageNum = PageInfoDto.DEFAULT_PAGE_NUM;
        int pageSize = PageInfoDto.DEFAULT_PAGE_SIZE;

        UserDto criteria = new UserDto();
        if (queryParams != null) {
            criteria.setUsername(queryParams.getUsername());
            criteria.setSocialSource(queryParams.getSocialSource());
            criteria.setEmail(queryParams.getEmail());

            if (queryParams.getPageNum() != null) {
                pageNum = queryParams.getPageNum();
            }
            if (queryParams.getPageSize() != null) {
                pageSize = queryParams.getPageSize();
            }
        }

        return userService.pageUser(pageNum, pageSize, criteria);
    }

}
