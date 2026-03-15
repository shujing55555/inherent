package com.example.demo.controller;

import com.example.demo.entity.InheritanceStory;
import com.example.demo.service.InheritanceStoryService;
import com.example.demo.service.UserBehaviorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 非遗传承故事前台接口：分页列表、详情
 */
@RestController
@RequestMapping("/api/stories")
public class InheritanceStoryController {

    private final InheritanceStoryService storyService;
    private final UserBehaviorService behaviorService;

    public InheritanceStoryController(InheritanceStoryService storyService,
                                      UserBehaviorService behaviorService) {
        this.storyService = storyService;
        this.behaviorService = behaviorService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InheritanceStory> result = storyService.list(null, region, page, size);
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
        InheritanceStory story = storyService.getById(id);
        if (userId != null) {
            behaviorService.logViewStory(userId, id);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", story));
    }
}
