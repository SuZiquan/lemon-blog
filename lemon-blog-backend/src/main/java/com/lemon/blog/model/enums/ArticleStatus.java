package com.lemon.blog.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ArticleStatus {
    PRIVATE(0),
    PUBLIC(1);

    private final Integer value;

    public static final Map<Integer, ArticleStatus> LOOKUP_BY_VALUE = new HashMap<>();

    static {
        for (ArticleStatus articleStatus : ArticleStatus.values()) {
            LOOKUP_BY_VALUE.put(articleStatus.getValue(), articleStatus);
        }
    }

    public static ArticleStatus fromValue(Integer value) {
        return LOOKUP_BY_VALUE.get(value);
    }

}