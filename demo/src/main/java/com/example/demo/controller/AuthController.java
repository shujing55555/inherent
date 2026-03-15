package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口：用户注册、用户登录、管理员登录
 * 前台登录与管理员登录区分：通过 login 的 admin 参数或单独 admin/login 区分
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 用户注册：注册后自动记录昵称、头像、注册时间 */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse resp = authService.register(req);
        return ResponseEntity.ok(Map.of("success", true, "data", resp));
    }

    /** 用户登录：admin=false 为普通用户，admin=true 为管理员（也可单独调 admin/login） */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(Map.of("success", true, "data", resp));
    }

    /** 管理员后台登录入口：仅 role=ADMIN 的用户可登录 */
    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@Valid @RequestBody LoginRequest req) {
        AuthResponse resp = authService.adminLogin(req);
        return ResponseEntity.ok(Map.of("success", true, "data", resp));
    }
}
