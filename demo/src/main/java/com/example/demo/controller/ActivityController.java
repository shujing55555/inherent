package com.example.demo.controller;

import com.example.demo.entity.Activity;
import com.example.demo.service.ActivityService;
import com.example.demo.service.UserBehaviorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 非遗活动前台接口：列表、详情、参加活动（报名需登录）
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final UserBehaviorService behaviorService;

    public ActivityController(ActivityService activityService,
                              UserBehaviorService behaviorService) {
        this.activityService = activityService;
        this.behaviorService = behaviorService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Activity> result = activityService.list(null, region, page, size);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        Activity activity = activityService.getById(id);
        boolean registered = userId != null && activityService.hasRegistered(userId, id);
        long count = activityService.countRegistrations(id);
        if (userId != null) {
            behaviorService.logViewActivity(userId, id);
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", activity,
                "registered", registered,
                "registrationCount", count
        ));
    }

    /** 参加活动：需登录，同一活动只能报一次 */
    @PostMapping("/{id}/register")
    public ResponseEntity<Map<String, Object>> register(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        activityService.register(userId, id);
        behaviorService.logRegisterActivity(userId, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "报名成功"));
    }

    /** 取消参加：需登录，取消当前用户对该活动的报名 */
    @DeleteMapping("/{id}/register")
    public ResponseEntity<Map<String, Object>> cancelRegister(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        activityService.cancelRegistration(userId, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "已取消报名"));
    }
}
