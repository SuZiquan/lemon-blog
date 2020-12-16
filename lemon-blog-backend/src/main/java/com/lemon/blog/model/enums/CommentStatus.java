package com.lemon.blog.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    UNAUDITED(0),
    AUDITED(1),
    REJECTED(2);

    private final Integer value;

    public static final Map<Integer, CommentStatus> LOOKUP_BY_VALUE = new HashMap<>();

    static {
        for (CommentStatus articleStatus : CommentStatus.values()) {
            LOOKUP_BY_VALUE.put(articleStatus.getValue(), articleStatus);
        }
    }

    public static CommentStatus fromValue(Integer value) {
        return LOOKUP_BY_VALUE.get(value);
    }

}