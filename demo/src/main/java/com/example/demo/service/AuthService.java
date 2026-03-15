package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务：用户注册、登录（含管理员登录）
 * 注册时自动记录昵称、头像、注册时间；管理员通过 admin 标识走独立校验
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /** 用户注册：仅允许注册为普通用户 */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("账号已存在");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new RuntimeException("手机号已被使用");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setNickname(req.getNickname() != null ? req.getNickname() : req.getUsername());
        user.setAvatar(req.getAvatar());
        user.setRole(UserRole.USER);
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return AuthResponse.of(token, user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), user.getRole());
    }

    /** 普通用户登录 */
    public AuthResponse login(LoginRequest req) {
        if (req.isAdmin()) {
            return adminLogin(req);
        }
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));
        if (user.getRole() != UserRole.USER) {
            throw new RuntimeException("请使用管理员入口登录");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return AuthResponse.of(token, user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), user.getRole());
    }

    /** 管理员登录：仅 role=ADMIN 的用户可登录后台 */
    public AuthResponse adminLogin(LoginRequest req) {
        User user = userRepository.findByUsernameAndRole(req.getUsername(), UserRole.ADMIN)
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return AuthResponse.of(token, user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), user.getRole());
    }
}
