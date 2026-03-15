package com.example.demo.controller;

import com.example.demo.config.LishuiRegions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 丽水市区县列表接口（供前端下拉、筛选使用）
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        return ResponseEntity.ok(Map.of("success", true, "data", LishuiRegions.ALL));
    }
}
