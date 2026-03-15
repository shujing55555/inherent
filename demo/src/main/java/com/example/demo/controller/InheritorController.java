package com.example.demo.controller;

import com.example.demo.entity.Inheritor;
import com.example.demo.service.InheritorService;
import com.example.demo.service.UserBehaviorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 非遗传承人前台接口：按姓名排序的列表、详情
 */
@RestController
@RequestMapping("/api/inheritors")
public class InheritorController {

    private final InheritorService inheritorService;
    private final UserBehaviorService behaviorService;

    public InheritorController(InheritorService inheritorService,
                               UserBehaviorService behaviorService) {
        this.inheritorService = inheritorService;
        this.behaviorService = behaviorService;
    }

    /** 全部传承人，可选区县筛选，按姓名排序 */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String region) {
        List<Inheritor> list = inheritorService.listAll(null, region);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id,
                                                       @AuthenticationPrincipal Long userId) {
        Inheritor inheritor = inheritorService.getById(id);
        if (userId != null) {
            behaviorService.logViewInheritor(userId, id);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", inheritor));
    }
}
