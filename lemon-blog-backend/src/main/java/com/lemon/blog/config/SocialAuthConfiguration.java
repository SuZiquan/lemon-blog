package com.lemon.blog.config;

import com.lemon.blog.exception.ApiException;
import com.lemon.blog.exception.ResultCode;
import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SocialAuthConfiguration {

    @Data
    public static class SocialAuthConfig {

        private String clientId;

        private String clientSecret;

        private String redirectUri;

    }

    @Bean
    @ConfigurationProperties("social-auth")
    public Map<String, SocialAuthConfig> socialAuthConfigs() {
        return new HashMap<>();
    }

    public AuthRequest getAuthRequest(String source) {
        Map<String, SocialAuthConfig> socialAuthConfigs = socialAuthConfigs();

        if (!socialAuthConfigs.containsKey(source)) {
            throw new ApiException(ResultCode.INVALID_PARAMS, "不支持的第三方登录平台");
        }

        SocialAuthConfig socialAuthConfig = socialAuthConfigs.get(source);
        return  new AuthGithubRequest(AuthConfig.builder()
                .clientId(socialAuthConfig.getClientId())
                .clientSecret(socialAuthConfig.getClientSecret())
                .redirectUri(socialAuthConfig.getRedirectUri())
                .build());
    }
}
