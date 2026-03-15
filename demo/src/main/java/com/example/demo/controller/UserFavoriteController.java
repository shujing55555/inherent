package com.example.demo.controller;

import com.example.demo.entity.IchProject;
import com.example.demo.service.UserFavoriteProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏非遗项目接口
 */
@RestController
@RequestMapping("/api/user/favorites/projects")
public class UserFavoriteController {

    private final UserFavoriteProjectService favoriteService;

    public UserFavoriteController(UserFavoriteProjectService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /** 当前用户收藏的非遗项目列表 */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        List<IchProject> projects = favoriteService.listFavorites(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", projects));
    }

    /** 添加收藏 */
    @PostMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> add(@PathVariable Long projectId,
                                                   @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        favoriteService.addFavorite(userId, projectId);
        return ResponseEntity.ok(Map.of("success", true, "message", "已收藏"));
    }

    /** 取消收藏 */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable Long projectId,
                                                      @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        favoriteService.removeFavorite(userId, projectId);
        return ResponseEntity.ok(Map.of("success", true, "message", "已取消收藏"));
    }
}

