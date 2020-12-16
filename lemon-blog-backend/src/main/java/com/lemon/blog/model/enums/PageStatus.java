package com.lemon.blog.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum PageStatus {
    PRIVATE(0),
    PUBLIC(1);

    private final Integer value;

    public static final Map<Integer, PageStatus> LOOKUP_BY_VALUE = new HashMap<>();

    static {
        for (PageStatus articleStatus : PageStatus.values()) {
            LOOKUP_BY_VALUE.put(articleStatus.getValue(), articleStatus);
        }
    }

    public static PageStatus fromValue(Integer value) {
        return LOOKUP_BY_VALUE.get(value);
    }

}