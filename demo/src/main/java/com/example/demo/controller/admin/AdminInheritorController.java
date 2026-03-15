package com.example.demo.controller.admin;

import com.example.demo.entity.Inheritor;
import com.example.demo.service.InheritorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台-非遗传承人管理
 */
@RestController
@RequestMapping("/api/admin/inheritors")
public class AdminInheritorController {

    private final InheritorService inheritorService;

    public AdminInheritorController(InheritorService inheritorService) {
        this.inheritorService = inheritorService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region) {
        List<Inheritor> list = inheritorService.listAll(keyword, region);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Inheritor inheritor = inheritorService.getById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", inheritor));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Inheritor inheritor) {
        Inheritor saved = inheritorService.create(inheritor);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Inheritor inheritor) {
        Inheritor updated = inheritorService.update(id, inheritor);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        inheritorService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
