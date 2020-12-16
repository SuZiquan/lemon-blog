package com.lemon.blog.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"),
    GUEST("GUEST");

    private final String value;

    public static final Map<String, UserRole> LOOKUP_BY_VALUE = new HashMap<>();

    static {
        for (UserRole userRole : UserRole.values()) {
            LOOKUP_BY_VALUE.put(userRole.getValue(), userRole);
        }
    }

    public static UserRole fromValue(String value) {
        return LOOKUP_BY_VALUE.get(value);
    }

}