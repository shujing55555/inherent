package com.example.demo.controller.admin;

import com.example.demo.entity.RecommendationWeight;
import com.example.demo.entity.UserBehavior;
import com.example.demo.entity.UserFavoriteProject;
import com.example.demo.entity.IchProject;
import com.example.demo.repository.UserBehaviorRepository;
import com.example.demo.repository.UserFavoriteProjectRepository;
import com.example.demo.repository.IchProjectRepository;
import com.example.demo.service.RecommendationWeightService;
import com.example.demo.service.UserBehaviorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台-推荐权重配置接口
 */
@RestController
@RequestMapping("/api/admin/recommend")
public class AdminRecommendationController {

    private final RecommendationWeightService weightService;
    private final UserBehaviorRepository behaviorRepository;
    private final UserFavoriteProjectRepository favoriteRepository;
    private final IchProjectRepository projectRepository;

    public AdminRecommendationController(RecommendationWeightService weightService,
                                         UserBehaviorRepository behaviorRepository,
                                         UserFavoriteProjectRepository favoriteRepository,
                                         IchProjectRepository projectRepository) {
        this.weightService = weightService;
        this.behaviorRepository = behaviorRepository;
        this.favoriteRepository = favoriteRepository;
        this.projectRepository = projectRepository;
    }

    /** 获取所有类型的推荐权重配置（若数据库为空则返回默认值） */
    @GetMapping("/weights")
    public Map<String, Object> listWeights() {
        List<RecommendationWeight> list = weightService.listAllOrDefault();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    /** 保存推荐权重配置（整体覆盖式） */
    @PostMapping("/weights")
    public ResponseEntity<Map<String, Object>> saveWeights(@RequestBody List<RecommendationWeight> weights) {
        weightService.saveAll(weights);
        return ResponseEntity.ok(Map.of("success", true, "message", "保存成功"));
    }

    /**
     * 推荐相关数据分析：按项目类型统计浏览与收藏次数
     */
    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        // 浏览：VIEW_PROJECT
        List<UserBehavior> views = behaviorRepository.findByType(UserBehaviorService.VIEW_PROJECT);
        Map<Long, Long> viewByProject = new HashMap<>();
        for (UserBehavior b : views) {
            if (b.getProjectId() == null) continue;
            viewByProject.merge(b.getProjectId(), 1L, Long::sum);
        }

        // 收藏
        List<UserFavoriteProject> favs = favoriteRepository.findAll();
        Map<Long, Long> favByProject = new HashMap<>();
        for (UserFavoriteProject f : favs) {
            if (f.getProjectId() == null) continue;
            favByProject.merge(f.getProjectId(), 1L, Long::sum);
        }

        // 合并项目 ID 集合
        java.util.Set<Long> projectIds = new java.util.HashSet<>();
        projectIds.addAll(viewByProject.keySet());
        projectIds.addAll(favByProject.keySet());

        Map<String, long[]> typeStats = new HashMap<>();
        if (!projectIds.isEmpty()) {
            List<IchProject> projects = projectRepository.findAllById(projectIds);
            for (IchProject p : projects) {
                String type = p.getIchType() != null && !p.getIchType().isBlank()
                        ? p.getIchType().trim()
                        : "未标注类型";
                long v = viewByProject.getOrDefault(p.getId(), 0L);
                long f = favByProject.getOrDefault(p.getId(), 0L);
                long[] arr = typeStats.computeIfAbsent(type, k -> new long[]{0L, 0L});
                arr[0] += v;
                arr[1] += f;
            }
        }

        java.util.List<Map<String, Object>> typeList = typeStats.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("ichType", e.getKey());
                    m.put("viewCount", e.getValue()[0]);
                    m.put("favoriteCount", e.getValue()[1]);
                    return m;
                })
                .sorted((a, b) -> Long.compare(
                        ((Number) b.get("viewCount")).longValue(),
                        ((Number) a.get("viewCount")).longValue()))
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("typeStats", typeList);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }
}

