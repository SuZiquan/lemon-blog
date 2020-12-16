package com.lemon.blog.controller;

import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.dto.AuthUser;
import com.lemon.blog.dto.UserDto;
import com.lemon.blog.dto.UsernameAndPassword;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.User;
import com.lemon.blog.service.UserService;
import com.lemon.blog.utils.AuthUtils;
import com.lemon.blog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@ApiController
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUtils authUtils;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public AuthUser login(@RequestBody UsernameAndPassword usernameAndPassword, HttpServletResponse response) {
        User user = userService.findUserByUserNameAndNotSocial(usernameAndPassword.getUsername());
        if (user == null || !passwordEncoder.matches(usernameAndPassword.getPassword(), user.getPassword())) {
            throw new ApiException(ResultCode.UNAUTHORIZED, "username or password error");
        }
        user.setLastLogin(LocalDateTime.now());
        userService.updateUser(user);
        AuthUser authUser = AuthUser.fromUser(user);
        response.setHeader(jwtConfig.getTokenHeader(), jwtUtils.createToken(authUser));
        return authUser;
    }


    @GetMapping("/userInfo")
    public AuthUser currentUserInfo() {
        return authUtils.getCurrentUser();
    }

}
