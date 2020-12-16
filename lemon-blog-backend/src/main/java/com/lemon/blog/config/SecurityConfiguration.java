package com.lemon.blog.config;

import com.lemon.blog.model.enums.UserRole;
import com.lemon.blog.security.exception.AccessDeniedHandler;
import com.lemon.blog.security.exception.UnauthorizedEntryPoint;
import com.lemon.blog.security.filter.JwtAuthenticationFilter;
import com.lemon.blog.security.provider.JwtAuthenticationProvider;
import com.lemon.blog.utils.AuthUtils;
import com.lemon.blog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthUtils authUtils;
    private final JwtUtils jwtUtils;
    private final JwtConfig jwtConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(new JwtAuthenticationProvider(jwtConfig, jwtUtils));
        return new ProviderManager(authenticationProviders);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.POST,
                        "/api/comments").authenticated()
                .antMatchers(
                        HttpMethod.DELETE,
                        "/api/comments/**").authenticated()
                .antMatchers(
                        HttpMethod.GET,
                        "/api/auth/userInfo").authenticated()
                .antMatchers(
                        "/api/auth/login",
                        "/api/oauth/login/**",
                        "/api/oauth/callback/**").permitAll()
                .antMatchers(
                        HttpMethod.GET,
                        "/api/overview/**",
                        "/api/articles/**",
                        "/api/comments/**",
                        "/api/categories/**",
                        "/api/tags/**",
                        "/api/pages/**",
                        "/api/users/*",
                        "/api/configs/**"
                        ).permitAll()
                .anyRequest().hasRole(UserRole.ADMIN.getValue())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtConfig, authUtils), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(new UnauthorizedEntryPoint(authUtils))
                .accessDeniedHandler(new AccessDeniedHandler(authUtils));

    }

}