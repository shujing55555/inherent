package com.example.demo.controller.admin;

import com.example.demo.entity.InheritanceStory;
import com.example.demo.service.InheritanceStoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台-非遗传承故事管理
 */
@RestController
@RequestMapping("/api/admin/stories")
public class AdminStoryController {

    private final InheritanceStoryService storyService;

    public AdminStoryController(InheritanceStoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<InheritanceStory> result = storyService.list(keyword, region, page, size);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        InheritanceStory story = storyService.getById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", story));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody InheritanceStory story) {
        InheritanceStory saved = storyService.create(story);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody InheritanceStory story) {
        InheritanceStory updated = storyService.update(id, story);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        storyService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
