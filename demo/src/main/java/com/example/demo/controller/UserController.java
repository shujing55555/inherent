package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 当前用户相关接口（需登录）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登录"));
        }
        UserDTO dto = userService.getCurrentUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    /** 当前用户修改个人信息（昵称、头像、密码可选，不可改账号与角色） */
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMe(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, Object> body) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登录"));
        }
        String nickname = body != null && body.containsKey("nickname") ? (String) body.get("nickname") : null;
        String avatar = body != null && body.containsKey("avatar") ? (String) body.get("avatar") : null;
        String newPassword = body != null && body.containsKey("newPassword") ? (String) body.get("newPassword") : null;
        try {
            UserDTO dto = userService.update(userId, nickname, avatar, newPassword, null, null, null);
            return ResponseEntity.ok(Map.of("success", true, "data", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
