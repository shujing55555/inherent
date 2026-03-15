package com.example.demo.controller.admin;

import com.example.demo.entity.IchProject;
import com.example.demo.service.IchProjectService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台-非遗项目管理：分页列表、增删改
 */
@RestController
@RequestMapping("/api/admin/projects")
public class AdminIchProjectController {

    private final IchProjectService projectService;

    public AdminIchProjectController(IchProjectService projectService) {
        this.projectService = projectService;
    }

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
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        IchProject project = projectService.getById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", project));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody IchProject project) {
        IchProject saved = projectService.create(project);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody IchProject project) {
        IchProject updated = projectService.update(id, project);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
