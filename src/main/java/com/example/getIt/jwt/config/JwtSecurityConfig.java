package com.example.getIt.jwt.config;

import com.example.getIt.jwt.JwtFilter;
import com.example.getIt.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 직접 만든 TokenProvider 와 JwtFilter 를 SecurityConfig 에 적용할 때 사용
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;
    // TokenProvider 를 주입받아서 JwtFilter 를 통해 Security 로직에 필터를 등록
    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(tokenProvider, redisTemplate);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}