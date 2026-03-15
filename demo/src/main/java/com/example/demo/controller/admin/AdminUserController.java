package com.example.demo.controller.admin;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台-用户管理：列表、新增、编辑、删除
 * 仅 ADMIN 可访问
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        UserRole roleFilter = null;
        if (role != null && !role.trim().isEmpty()) {
            if ("ADMIN".equalsIgnoreCase(role.trim())) roleFilter = UserRole.ADMIN;
            else if ("USER".equalsIgnoreCase(role.trim())) roleFilter = UserRole.USER;
        }
        List<UserDTO> list = userService.listAll(keyword, roleFilter);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String nickname = (String) body.get("nickname");
        String avatar = (String) body.get("avatar");
        String email = (String) body.get("email");
        String phone = (String) body.get("phone");
        String roleStr = (String) body.get("role");
        UserRole role = roleStr != null && "ADMIN".equalsIgnoreCase(roleStr) ? UserRole.ADMIN : UserRole.USER;
        UserDTO dto = userService.create(username, password, nickname, avatar, email, phone, role);
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String nickname = (String) body.get("nickname");
        String avatar = (String) body.get("avatar");
        String newPassword = (String) body.get("password");
        String email = (String) body.get("email");
        String phone = (String) body.get("phone");
        String roleStr = (String) body.get("role");
        UserRole role = roleStr != null && "ADMIN".equalsIgnoreCase(roleStr) ? UserRole.ADMIN : null;
        if (role == null && body.containsKey("role")) {
            role = "USER".equalsIgnoreCase(roleStr) ? UserRole.USER : role;
        }
        UserDTO dto = userService.update(id, nickname, avatar, newPassword, email, phone, role);
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
