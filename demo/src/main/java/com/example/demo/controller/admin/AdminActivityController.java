package com.example.demo.controller.admin;

import com.example.demo.entity.Activity;
import com.example.demo.service.ActivityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台-非遗活动管理
 */
@RestController
@RequestMapping("/api/admin/activities")
public class AdminActivityController {

    private final ActivityService activityService;
    private final ObjectMapper objectMapper;

    public AdminActivityController(ActivityService activityService, ObjectMapper objectMapper) {
        this.activityService = activityService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Activity> result = activityService.list(keyword, region, page, size);
        List<Map<String, Object>> data = result.getContent().stream()
                .map(a -> {
                    Map<String, Object> map = objectMapper.convertValue(a, new TypeReference<Map<String, Object>>() {});
                    map.put("registrationCount", activityService.countRegistrations(a.getId()));
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", data,
                "total", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", activity));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Activity activity) {
        Activity saved = activityService.create(activity);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Activity activity) {
        Activity updated = activityService.update(id, activity);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        activityService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
