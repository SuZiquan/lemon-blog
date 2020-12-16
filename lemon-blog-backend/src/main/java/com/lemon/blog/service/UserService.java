package com.lemon.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lemon.blog.dao.UserMapper;
import com.lemon.blog.dto.PageInfoDto;
import com.lemon.blog.dto.UserDto;
import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import com.lemon.blog.model.User;
import com.lemon.blog.model.enums.UserRole;
import com.lemon.blog.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @CacheEvict(cacheNames = "user", key = "#userId")
    public void changeUsername(Long userId, String newUsername) {
        userMapper.updateUsernameById(userId, newUsername);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        String encodedOldPassword = userMapper.selectOne(Wrappers.<User>query().eq("id", userId).select("password")).getPassword();
        if (encodedOldPassword == null || !passwordEncoder.matches(oldPassword, encodedOldPassword)) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "旧密码错误");
        }
        userMapper.updatePasswordById(userId, passwordEncoder.encode(newPassword));
    }

    public User findUserByUserNameAndNotSocial(String username) {
        return userMapper.selectOne(Wrappers.<User>query()
                .eq("username", username)
                .isNull("social_source"));
    }

    public User findUserBySocialSourceAndUserId(String socialSource, String socialUserId) {
        return userMapper.selectOne(Wrappers.<User>query()
                .eq("social_source", socialSource)
                .eq("social_user_id", socialUserId));
    }

    public void createUser(User user) {
        userMapper.insert(user);
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = "user", key = "#user.id"),
            @CacheEvict(cacheNames = "user_paging", allEntries = true)
    })
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    public List<User> getAdmins() {
        return userMapper.selectList(Wrappers.<User>query()
                .eq("role", UserRole.ADMIN.getValue())
                .select(User.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("password")));
    }

    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    public UserDto getUserById(Long id) {
        User user = userMapper.selectOne(Wrappers.<User>query()
                .eq("id", id)
                .select(User.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("password")));
        if (user == null) {
            return null;
        }
        return BeanUtils.convertTo(user, UserDto.class);
    }

    @Cacheable(cacheNames = "user_paging")
    public PageInfoDto<UserDto> pageUser(Integer pageNum, Integer pageSize, UserDto criteria) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        if (criteria != null) {
            queryWrapper
                    .eq(criteria.getId() != null, "id", criteria.getId())
                    .like(StringUtils.isNotEmpty(criteria.getUsername()), "username", criteria.getUsername())
                    .like(StringUtils.isNotEmpty(criteria.getEmail()), "email", criteria.getEmail())
                    .eq(StringUtils.isNotEmpty(criteria.getSocialSource()), "social_source", criteria.getSocialSource());
        }

        queryWrapper.orderByDesc("last_login")
                .select(User.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("password"));
        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        long total = userPage.getTotal();
        List<UserDto> userDtos = userPage.getRecords()
                .stream()
                .map(user -> BeanUtils.convertTo(user, UserDto.class))
                .collect(Collectors.toList());
        return new PageInfoDto<>((int) total, pageNum, pageSize, userDtos);
    }

}