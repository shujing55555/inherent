package com.example.demo.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 后台-文件上传
 * 图片保存到 file.upload-dir（默认 src/main/resources/pictures），文件名与用户选择的图片名一致；
 * 返回可访问路径 /pictures/文件名，该路径存入数据库用于展示。
 */
@RestController
@RequestMapping("/api/upload")
public class AdminUploadController {

    @Value("${file.upload-dir:src/main/resources/pictures}")
    private String uploadDir;

    /** 上传单文件：保存到 upload-dir，文件名保留用户选择的名称（重名则追加 _1、_2...）；返回 URL 如 /pictures/xxx.jpg */
    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "文件为空"));
        }
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String safeName = sanitizeFilename(file.getOriginalFilename());
            if (safeName == null || safeName.isEmpty()) {
                safeName = "image";
            }
            String filename = uniqueFilename(dir, safeName);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            String url = "/pictures/" + filename;
            return ResponseEntity.ok(Map.of("success", true, "url", url));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "上传失败：" + e.getMessage()));
        }
    }

    /** 去掉路径、只保留安全文件名 */
    private String sanitizeFilename(String name) {
        if (name == null) return null;
        name = name.replaceAll("[/\\\\]", "").trim();
        return name.isEmpty() ? null : name;
    }

    /** 若同名文件已存在，则追加 _1、_2 ... */
    private String uniqueFilename(Path dir, String baseName) {
        if (!Files.exists(dir.resolve(baseName))) {
            return baseName;
        }
        int dot = baseName.lastIndexOf('.');
        String prefix = dot > 0 ? baseName.substring(0, dot) : baseName;
        String suffix = dot > 0 ? baseName.substring(dot) : "";
        for (int i = 1; ; i++) {
            String candidate = prefix + "_" + i + suffix;
            if (!Files.exists(dir.resolve(candidate))) {
                return candidate;
            }
        }
    }
}
