package com.example.demo.controller;

import com.example.demo.dto.AiConsultRequest;
import com.example.demo.dto.AiConsultResponse;
import com.example.demo.service.DeepSeekService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 非遗 AI 咨询接口：用户提问，返回 DeepSeek 生成的回答
 * 需登录后使用，便于区分用户与防止滥用
 */
@RestController
@RequestMapping("/api/ai")
public class AiConsultController {

    private final DeepSeekService deepSeekService;

    public AiConsultController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/consult")
    public ResponseEntity<Map<String, Object>> consult(
            @Valid @RequestBody AiConsultRequest req,
            @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "请先登录"));
        }
        AiConsultResponse resp = deepSeekService.chat(req.getQuestion());
        return ResponseEntity.ok(Map.of("success", true, "data", resp));
    }
}
