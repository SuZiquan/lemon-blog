package com.lemon.blog.controller;

import com.google.common.collect.ImmutableMap;
import com.lemon.blog.config.JwtConfig;
import com.lemon.blog.config.SocialAuthConfiguration;
import com.lemon.blog.dto.AuthUser;
import com.lemon.blog.dto.UserDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.User;
import com.lemon.blog.model.enums.UserRole;
import com.lemon.blog.service.UserService;
import com.lemon.blog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

@ApiController
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class SocialAuthController {

    private final SocialAuthConfiguration socialAuthConfiguration;

    private final UserService userService;

    private final JwtUtils jwtUtils;

    private final JwtConfig jwtConfig;

    @GetMapping("/login/{source}")
    public Map<String, String> login(@PathVariable("source") String source) {
        AuthRequest authRequest = socialAuthConfiguration.getAuthRequest(source);
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        return ImmutableMap.of("authorizeUrl", authorizeUrl);
    }

    @RequestMapping("/callback/{source}")
    public AuthUser callback(@PathVariable("source") String source,
                            HttpServletRequest request, HttpServletResponse response) {
        AuthCallback authCallback = AuthCallback.builder()
                .code(request.getParameter("code"))
                .auth_code(request.getParameter("auth_code"))
                .state(request.getParameter("state"))
                .authorization_code(request.getParameter("authorization_code"))
                .oauth_token(request.getParameter("oauth_token"))
                .oauth_verifier(request.getParameter("oauth_verifier")).build();

        AuthResponse socialAuthResponse = socialAuthConfiguration.getAuthRequest(source)
                .login(authCallback);

        if (!socialAuthResponse.ok()) {
            throw new ApiException(ResultCode.UNAUTHORIZED, socialAuthResponse.getMsg());
        }
        me.zhyd.oauth.model.AuthUser responseUser = (me.zhyd.oauth.model.AuthUser) socialAuthResponse.getData();
        String socialUserId = responseUser.getUuid();
        String username = responseUser.getUsername();
        String avatarUrl = responseUser.getAvatar();
        String email = responseUser.getEmail();

        User user = new User();
        user.setSocialSource(source);
        user.setSocialUserId(socialUserId);
        user.setUsername(username);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setRole(UserRole.GUEST.getValue());
        user.setLastLogin(LocalDateTime.now());

        User userStored = userService.findUserBySocialSourceAndUserId(source, socialUserId);
        if (userStored == null) {
            userService.createUser(user);
        } else {
            user.setId(userStored.getId());
            userService.updateUser(user);
        }

        AuthUser authUser = AuthUser.fromUser(user);
        response.setHeader(jwtConfig.getTokenHeader(), jwtUtils.createToken(authUser));
        return authUser;
    }
}