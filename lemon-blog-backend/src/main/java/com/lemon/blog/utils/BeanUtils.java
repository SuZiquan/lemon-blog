package com.lemon.blog.utils;

import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;

public class BeanUtils {

    public static <T> T convertTo(Object src, Class<T> dstClass) {
        try {
            T dst = dstClass.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(src, dst);
            return dst;
        } catch (Exception e) {
            throw new ApiException(ResultCode.SERVER_ERROR);
        }
    }

}
