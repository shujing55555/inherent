package com.example.demo.controller.admin;

import com.example.demo.dto.AdminOverviewStats;
import com.example.demo.dto.AdminOverviewStats.LabelCount;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.IchProjectRepository;
import com.example.demo.repository.NewsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserBehaviorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台-统计分析接口：为后台首页提供数据分析数据
 */
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final UserRepository userRepository;
    private final IchProjectRepository projectRepository;
    private final ActivityRepository activityRepository;
    private final NewsRepository newsRepository;
    private final UserBehaviorService behaviorService;

    public AdminStatsController(UserRepository userRepository,
                                IchProjectRepository projectRepository,
                                ActivityRepository activityRepository,
                                NewsRepository newsRepository,
                                UserBehaviorService behaviorService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.activityRepository = activityRepository;
        this.newsRepository = newsRepository;
        this.behaviorService = behaviorService;
    }

    /** 后台首页：总体数量 + 区县分布分析 */
    @GetMapping("/overview")
    public Map<String, Object> overview() {
        AdminOverviewStats stats = new AdminOverviewStats();
        stats.setUserCount(userRepository.count());
        stats.setProjectCount(projectRepository.count());
        stats.setActivityCount(activityRepository.count());
        stats.setNewsCount(newsRepository.count());

        // 非遗项目按区县分布（category 字段存区县）
        Map<String, Long> projectRegionMap = projectRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> normalizeRegion(p.getCategory()),
                        Collectors.counting()
                ));
        List<LabelCount> projectByRegion = projectRegionMap.entrySet().stream()
                .map(e -> new LabelCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(LabelCount::getCount).reversed())
                .collect(Collectors.toList());
        stats.setProjectByRegion(projectByRegion);

        // 活动按区县分布
        Map<String, Long> activityRegionMap = activityRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> normalizeRegion(a.getRegion()),
                        Collectors.counting()
                ));
        List<LabelCount> activityByRegion = activityRegionMap.entrySet().stream()
                .map(e -> new LabelCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(LabelCount::getCount).reversed())
                .collect(Collectors.toList());
        stats.setActivityByRegion(activityByRegion);

        // 为保持与其它后台接口一致，封装 success/data 结构，
        // 避免使用 Map.of 引起的复杂泛型推断问题，改用 HashMap。
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * 行为统计：基于 user_behavior 的可视化数据
     * - projectViewsByRegion：各区县项目浏览量
     * - projectViewsByType：不同项目类型的浏览量
     * - projectViewsTrend：最近 30 天浏览趋势
     */
    @GetMapping("/behavior")
    public Map<String, Object> behavior() {
        Map<String, Object> data = new HashMap<>();
        data.put("projectViewsByRegion", behaviorService.projectViewsByRegion());
        data.put("projectViewsByType", behaviorService.projectViewsByType());
        data.put("projectViewsTrend", behaviorService.projectViewsTrend(30));

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    private String normalizeRegion(String region) {
        if (region == null) return "未填写";
        String r = region.trim();
        return r.isEmpty() ? "未填写" : r;
    }
}

