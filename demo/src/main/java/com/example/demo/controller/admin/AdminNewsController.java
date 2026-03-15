package com.example.demo.controller.admin;

import com.example.demo.entity.News;
import com.example.demo.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台-非遗资讯管理
 */
@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<News> result = newsService.list(keyword, region, page, size);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        return ResponseEntity.ok(Map.of("success", true, "data", news));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody News news) {
        News saved = newsService.create(news);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody News news) {
        News updated = newsService.update(id, news);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
    }
}
