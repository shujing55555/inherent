package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * - 前台接口：部分公开（列表、详情），部分需登录（报名活动、AI 咨询、个人资料）
 * - 后台接口：/api/admin/** 仅允许 ADMIN 角色
 * - 无状态：使用 JWT，不创建 Session
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 明文密码，不加密（仅用于学习或内网环境，生产环境请使用 BCrypt 等加密）
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开：登录、注册、管理员登录
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/admin/login").permitAll()
                        // 公开：前台展示类接口（列表、详情）
                        .requestMatchers(HttpMethod.GET,
                                "/api/projects/**", "/api/inheritors/**", "/api/news/**",
                                "/api/activities/**", "/api/stories/**", "/api/regions").permitAll()
                        // 后台所有接口需 ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 文件上传/访问：登录用户均可上传（个人头像等），管理员在后台也用此接口
                        .requestMatchers("/api/upload/**").authenticated()
                        .requestMatchers("/pictures/**", "/uploads/**").permitAll()
                        // 其余 /api 开头的需要认证（用户登录即可）
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
