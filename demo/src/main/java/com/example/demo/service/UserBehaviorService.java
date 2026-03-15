package com.example.demo.service;

import com.example.demo.dto.AdminOverviewStats;
import com.example.demo.entity.IchProject;
import com.example.demo.entity.UserBehavior;
import com.example.demo.repository.IchProjectRepository;
import com.example.demo.repository.UserBehaviorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为记录与统计服务
 */
@Service
public class UserBehaviorService {

    public static final String VIEW_PROJECT = "VIEW_PROJECT";
    public static final String VIEW_INHERITOR = "VIEW_INHERITOR";
    public static final String VIEW_STORY = "VIEW_STORY";
    public static final String VIEW_ACTIVITY = "VIEW_ACTIVITY";
    public static final String REGISTER_ACTIVITY = "REGISTER_ACTIVITY";

    private final UserBehaviorRepository behaviorRepository;
    private final IchProjectRepository projectRepository;

    public UserBehaviorService(UserBehaviorRepository behaviorRepository,
                               IchProjectRepository projectRepository) {
        this.behaviorRepository = behaviorRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void logViewProject(Long userId, Long projectId) {
        UserBehavior b = new UserBehavior();
        b.setUserId(userId);
        b.setType(VIEW_PROJECT);
        b.setProjectId(projectId);
        behaviorRepository.save(b);
    }

    @Transactional
    public void logViewInheritor(Long userId, Long inheritorId) {
        UserBehavior b = new UserBehavior();
        b.setUserId(userId);
        b.setType(VIEW_INHERITOR);
        behaviorRepository.save(b);
    }

    @Transactional
    public void logViewStory(Long userId, Long storyId) {
        UserBehavior b = new UserBehavior();
        b.setUserId(userId);
        b.setType(VIEW_STORY);
        behaviorRepository.save(b);
    }

    @Transactional
    public void logViewActivity(Long userId, Long activityId) {
        UserBehavior b = new UserBehavior();
        b.setUserId(userId);
        b.setType(VIEW_ACTIVITY);
        behaviorRepository.save(b);
    }

    @Transactional
    public void logRegisterActivity(Long userId, Long activityId) {
        UserBehavior b = new UserBehavior();
        b.setUserId(userId);
        b.setType(REGISTER_ACTIVITY);
        behaviorRepository.save(b);
    }

    /**
     * 图表 1：各区县非遗项目浏览量（按 VIEW_PROJECT 统计）
     */
    @Transactional(readOnly = true)
    public List<AdminOverviewStats.LabelCount> projectViewsByRegion() {
        List<UserBehavior> views = behaviorRepository.findByType(VIEW_PROJECT);
        if (views.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> projectViewCount = views.stream()
                .filter(b -> b.getProjectId() != null)
                .collect(Collectors.groupingBy(UserBehavior::getProjectId, Collectors.counting()));

        if (projectViewCount.isEmpty()) {
            return List.of();
        }
        List<IchProject> projects = projectRepository.findAllById(projectViewCount.keySet());
        Map<String, Long> regionCount = new HashMap<>();
        for (IchProject p : projects) {
            String region = p.getCategory() != null && !p.getCategory().isBlank() ? p.getCategory().trim() : "未填写";
            long count = projectViewCount.getOrDefault(p.getId(), 0L);
            regionCount.merge(region, count, Long::sum);
        }
        return regionCount.entrySet().stream()
                .map(e -> new AdminOverviewStats.LabelCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(AdminOverviewStats.LabelCount::getCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 图表 2：不同 ich_type 的访问量（按 VIEW_PROJECT 统计）
     */
    @Transactional(readOnly = true)
    public List<AdminOverviewStats.LabelCount> projectViewsByType() {
        List<UserBehavior> views = behaviorRepository.findByType(VIEW_PROJECT);
        if (views.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> projectViewCount = views.stream()
                .filter(b -> b.getProjectId() != null)
                .collect(Collectors.groupingBy(UserBehavior::getProjectId, Collectors.counting()));

        if (projectViewCount.isEmpty()) {
            return List.of();
        }

        List<IchProject> projects = projectRepository.findAllById(projectViewCount.keySet());
        Map<String, Long> typeCount = new HashMap<>();
        for (IchProject p : projects) {
            String type = p.getIchType() != null && !p.getIchType().isBlank() ? p.getIchType().trim() : "未标注类型";
            long count = projectViewCount.getOrDefault(p.getId(), 0L);
            typeCount.merge(type, count, Long::sum);
        }

        return typeCount.entrySet().stream()
                .map(e -> new AdminOverviewStats.LabelCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(AdminOverviewStats.LabelCount::getCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 图表 3：一段时间内（最近 days 天）项目浏览趋势
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> projectViewsTrend(int days) {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(days - 1L);
        LocalDateTime from = LocalDateTime.of(fromDate, LocalTime.MIN);
        List<UserBehavior> views = behaviorRepository.findByTypeAndCreateTimeAfterOrderByCreateTimeAsc(VIEW_PROJECT, from);

        Map<LocalDate, Long> dateCount = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = fromDate.plusDays(i);
            dateCount.put(d, 0L);
        }
        for (UserBehavior b : views) {
            LocalDate d = b.getCreateTime().toLocalDate();
            if (!d.isBefore(fromDate) && !d.isAfter(today)) {
                dateCount.merge(d, 1L, Long::sum);
            }
        }

        return dateCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("date", e.getKey().toString());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    /**
     * 当前用户最近浏览过的项目行为
     */
    @Transactional(readOnly = true)
    public List<UserBehavior> recentProjectViews(Long userId, int limit) {
        if (userId == null) {
            return List.of();
        }
        return behaviorRepository.findTop100ByUserIdAndTypeOrderByCreateTimeDesc(userId, VIEW_PROJECT);
    }
}

