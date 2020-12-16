package com.lemon.blog.controller;

import com.lemon.blog.dto.AccountDto;
import com.lemon.blog.service.UserService;
import com.lemon.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AuthUtils authUtils;

    private final UserService userService;

    @PostMapping("/username")
    public void changeUsername(@RequestBody AccountDto accountDto) {
        userService.changeUsername(authUtils.getCurrentUser().getId(), accountDto.getUsername());
    }

    @PostMapping("/password")
    public void changePassword(@RequestBody AccountDto accountDto) {
        userService.changePassword(authUtils.getCurrentUser().getId(), accountDto.getOldPassword(), accountDto.getPassword());
    }

}