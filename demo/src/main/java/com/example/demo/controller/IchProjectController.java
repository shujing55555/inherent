package com.example.demo.controller;

import com.example.demo.entity.IchProject;
import com.example.demo.service.IchProjectService;
import com.example.demo.service.RecommendationService;
import com.example.demo.service.UserBehaviorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 非遗项目前台接口：列表（支持关键词搜索）、详情
 * 所有 GET 在 Security 中已放行
 */
@RestController
@RequestMapping("/api/projects")
public class IchProjectController {

    private final IchProjectService projectService;
    private final UserBehaviorService behaviorService;
    private final RecommendationService recommendationService;

    public IchProjectController(IchProjectService projectService,
                                UserBehaviorService behaviorService,
                                RecommendationService recommendationService) {
        this.projectService = projectService;
        this.behaviorService = behaviorService;
        this.recommendationService = recommendationService;
    }

    /** 分页列表；keyword / region / heritageLevel / ichType 可选，区县为丽水市所辖区县 */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String heritageLevel,
            @RequestParam(required = false) String ichType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<IchProject> result = projectService.list(keyword, region, heritageLevel, ichType, page, size);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id,
                                                       @AuthenticationPrincipal Long userId) {
        IchProject project = projectService.getById(id);
        // 记录浏览行为（即使未登录也可记录匿名行为，这里仅记录已登录用户）
        if (userId != null) {
            behaviorService.logViewProject(userId, id);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", project));
    }

    /**
     * 推荐当前用户可能感兴趣的非遗项目：
     * - 已登录：基于浏览 / 收藏和项目类型推荐
     * - 未登录：返回全局热门项目
     */
    @GetMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@AuthenticationPrincipal Long userId) {
        java.util.List<IchProject> list = recommendationService.recommendProjects(userId, 6);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }
}
