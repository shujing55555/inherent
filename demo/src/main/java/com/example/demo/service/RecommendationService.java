package com.example.demo.service;

import com.example.demo.entity.IchProject;
import com.example.demo.entity.UserBehavior;
import com.example.demo.entity.UserFavoriteProject;
import com.example.demo.entity.RecommendationWeight;
import com.example.demo.repository.IchProjectRepository;
import com.example.demo.repository.UserFavoriteProjectRepository;
import com.example.demo.repository.RecommendationWeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于浏览 / 收藏和项目类型的推荐服务
 */
@Service
public class RecommendationService {

    private final UserBehaviorService behaviorService;
    private final UserFavoriteProjectRepository favoriteRepository;
    private final IchProjectRepository projectRepository;
    private final RecommendationWeightRepository weightRepository;

    public RecommendationService(UserBehaviorService behaviorService,
                                 UserFavoriteProjectRepository favoriteRepository,
                                 IchProjectRepository projectRepository,
                                 RecommendationWeightRepository weightRepository) {
        this.behaviorService = behaviorService;
        this.favoriteRepository = favoriteRepository;
        this.projectRepository = projectRepository;
        this.weightRepository = weightRepository;
    }
    /** 从数据库加载权重配置 */
    private Map<String, RecommendationWeight> loadWeights() {
        List<RecommendationWeight> list = weightRepository.findAll();
        Map<String, RecommendationWeight> map = new HashMap<>();
        for (RecommendationWeight w : list) {
            map.put(normalizeType(w.getIchType()), w);
        }
        return map;
    }

    private String normalizeType(String type) {
        if (type == null) return "未标注类型";
        String t = type.trim();
        return t.isEmpty() ? "未标注类型" : t;
    }

    private RecommendationWeight buildWeight(String type, double base, double view, double fav) {
        RecommendationWeight w = new RecommendationWeight();
        w.setIchType(type);
        w.setBaseWeight(base);
        w.setViewWeight(view);
        w.setFavoriteWeight(fav);
        return w;
    }

    /**
     * 为用户推荐非遗项目：
     * - 初始权重（新用户无行为时生效）：
     *   民间文学 1；传统体育、游艺与杂技 0.5；传统技艺 2；其余类型 0
     * - 浏览权重：每次浏览某类型 +1
     * - 收藏权重：每次收藏某类型 +5
     * - 最终按类型权重排序：
     *   - 第 1 名类型推荐 3 个项目
     *   - 第 2 名类型推荐 2 个项目
     *   - 第 3 名类型推荐 1 个项目
     *   - 同一类型内部随机挑选；永远不推荐已浏览/已收藏项目
     */
    @Transactional(readOnly = true)
    public List<IchProject> recommendProjects(Long userId, int limit) {
        // 初始化：所有类型的基础权重（无行为时也有偏好）
        Map<String, RecommendationWeight> weightConfig = loadWeights();
        Map<String, Double> typeScore = new HashMap<>();
        for (Map.Entry<String, RecommendationWeight> e : weightConfig.entrySet()) {
            typeScore.put(e.getKey(), e.getValue().getBaseWeight());
        }
        // 最近浏览记录（仅登录用户有）
        List<UserBehavior> recentViews =
                (userId != null) ? behaviorService.recentProjectViews(userId, 100) : List.of();
        // 收藏记录（仅登录用户有）
        List<UserFavoriteProject> favorites =
                (userId != null) ? favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId) : List.of();

        Set<Long> seenProjectIds = new HashSet<>();
        recentViews.stream()
                .map(UserBehavior::getProjectId)
                .filter(Objects::nonNull)
                .forEach(seenProjectIds::add);
        favorites.stream()
                .map(UserFavoriteProject::getProjectId)
                .filter(Objects::nonNull)
                .forEach(seenProjectIds::add);

        // 浏览权重：每次浏览所在类型 + viewWeight
        for (UserBehavior b : recentViews) {
            Long pid = b.getProjectId();
            if (pid == null) continue;
            projectRepository.findById(pid).ifPresent(p -> {
                String type = normalizeType(p.getIchType());
                RecommendationWeight cfg = weightConfig.get(type);
                if (cfg == null) {
                    cfg = weightConfig.get("未标注类型");
                }
                if (cfg == null) {
                    cfg = buildWeight("未标注类型", 0.0, 1.0, 5.0);
                }
                double inc = cfg.getViewWeight();
                typeScore.merge(type, inc, Double::sum);
            });
        }
        // 收藏权重：每次收藏所在类型 + favoriteWeight
        for (UserFavoriteProject fav : favorites) {
            Long pid = fav.getProjectId();
            if (pid == null) continue;
            projectRepository.findById(pid).ifPresent(p -> {
                String type = normalizeType(p.getIchType());
                RecommendationWeight cfg = weightConfig.get(type);
                if (cfg == null) {
                    cfg = weightConfig.get("未标注类型");
                }
                if (cfg == null) {
                    cfg = buildWeight("未标注类型", 0.0, 1.0, 5.0);
                }
                double inc = cfg.getFavoriteWeight();
                typeScore.merge(type, inc, Double::sum);
            });
        }
        // 类型按总权重排序，取前 3 个类型
        List<String> sortedTypes = typeScore.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        int[] slots = new int[]{3, 2, 1};
        List<IchProject> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < slots.length && i < sortedTypes.size(); i++) {
            if (result.size() >= limit) break;
            String type = sortedTypes.get(i);
            int need = slots[i];
            if (need <= 0) continue;
            List<IchProject> candidates = projectRepository.findTop20ByIchTypeOrderByApprovalTimeDesc(type);
            // 过滤掉已看/已收藏和已经加入结果的项目
            List<IchProject> filtered = candidates.stream()
                    .filter(p -> !seenProjectIds.contains(p.getId()))
                    .filter(p -> result.stream().noneMatch(x -> x.getId().equals(p.getId())))
                    .collect(Collectors.toList());
            // 随机打乱，同一类型每次刷新可能推荐不同项目
            Collections.shuffle(filtered, random);
            for (IchProject p : filtered) {
                if (result.size() >= limit) break;
                if (need <= 0) break;
                result.add(p);
                need--;
            }
        }

        // 若仍不足 limit，用其它类型或全局热门补足（仍不推荐已看/已收藏）
        if (result.size() < limit) {
            for (IchProject p : projectRepository.findTop20ByOrderByApprovalTimeDesc()) {
                if (result.size() >= limit) break;
                if (seenProjectIds.contains(p.getId())) continue;
                if (result.stream().anyMatch(x -> x.getId().equals(p.getId()))) continue;
                result.add(p);
            }
        }

        return result;
    }
}

