package com.lemon.blog;

import com.lemon.blog.dto.PageInfoDto;
import com.lemon.blog.model.User;
import com.lemon.blog.model.enums.UserRole;
import com.lemon.blog.service.ArticleService;
import com.lemon.blog.service.CategoryService;
import com.lemon.blog.service.TagService;
import com.lemon.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@MapperScan("com.lemon.blog.dao")
@Slf4j
@EnableCaching
public class LemonBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(LemonBlogApplication.class, args);
    }

    @Order(1)
    @Component
    @RequiredArgsConstructor
    public static class CreateAdminAccountIfAbsent implements CommandLineRunner {

        private final UserService userService;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
            List<User> admins = userService.getAdmins();
            if (!admins.isEmpty()) {
                List<String> adminUserNames = admins.stream().map(User::getUsername).collect(Collectors.toList());
                log.info("existed admin account: {}", adminUserNames);
            } else {
                String username = "admin";
                String password = "root";
                log.info("no existed admin account, create one, username: {}, password: {}", username, password);
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(UserRole.ADMIN.getValue());
                userService.createUser(user);
            }
        }
    }

    @Order(0)
    @Component
    @RequiredArgsConstructor
    public static class CacheWarmUp implements CommandLineRunner {

        private final ArticleService articleService;
        private final CategoryService categoryService;
        private final TagService tagService;

        @Override
        public void run(String... args) {
            articleService.pageArticle(PageInfoDto.DEFAULT_PAGE_NUM, PageInfoDto.DEFAULT_PAGE_SIZE, null);
            categoryService.getCategoriesWithArticleCount();
            tagService.getTagsWithArticleCount();
        }
    }
}
