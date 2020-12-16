package com.lemon.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.blog.model.User;
import com.lemon.blog.model.enums.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Data
public class AuthUser {

    private Long id;

    private String username;

    private String avatarUrl;

    private String socialSource;

    private String role;

    public boolean hasRole(UserRole userRole) {
        return role != null && role.equals(userRole.getValue());
    }


    public static AuthUser fromUser(User user) {
        AuthUser authUser = new AuthUser();
        authUser.setId(user.getId());
        authUser.setUsername(user.getUsername());
        authUser.setSocialSource(user.getSocialSource());
        authUser.setAvatarUrl(user.getAvatarUrl());
        authUser.setRole(user.getRole());
        return authUser;
    }


    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role);
    }

}