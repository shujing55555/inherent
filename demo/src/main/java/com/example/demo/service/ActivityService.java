package com.example.demo.service;

import com.example.demo.entity.Activity;
import com.example.demo.entity.ActivityRegistration;
import com.example.demo.repository.ActivityRegistrationRepository;
import com.example.demo.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 非遗活动服务：列表、详情、报名（参加活动）、后台增删改
 */
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityRegistrationRepository registrationRepository;

    public ActivityService(ActivityRepository activityRepository,
                           ActivityRegistrationRepository registrationRepository) {
        this.activityRepository = activityRepository;
        this.registrationRepository = registrationRepository;
    }

    /** 分页列表：关键字(标题/内容/地点)、区县 可组合筛选 */
    public Page<Activity> list(String keyword, String region, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String k = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String r = (region != null && !region.trim().isEmpty()) ? region.trim() : null;
        return activityRepository.searchByConditions(k, r, pageable);
    }

    public Activity getById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
    }

    /** 用户报名：仅在报名开始时间(startTime)与报名结束时间(endTime)之间可报名 */
    @Transactional
    public void register(Long userId, Long activityId) {
        if (registrationRepository.findByUserIdAndActivityId(userId, activityId).isPresent()) {
            throw new RuntimeException("您已报名过该活动");
        }
        Activity activity = getById(activityId);
        LocalDateTime now = LocalDateTime.now();
        if (activity.getStartTime() != null && now.isBefore(activity.getStartTime())) {
            throw new RuntimeException("报名未开始，暂不可报名");
        }
        if (activity.getEndTime() != null && now.isAfter(activity.getEndTime())) {
            throw new RuntimeException("报名已结束，不可报名");
        }
        ActivityRegistration r = new ActivityRegistration();
        r.setUserId(userId);
        r.setActivityId(activityId);
        registrationRepository.save(r);
    }

    /** 检查当前用户是否已报名该活动 */
    public boolean hasRegistered(Long userId, Long activityId) {
        return registrationRepository.findByUserIdAndActivityId(userId, activityId).isPresent();
    }

    /** 取消报名 */
    @Transactional
    public void cancelRegistration(Long userId, Long activityId) {
        registrationRepository.findByUserIdAndActivityId(userId, activityId)
                .ifPresent(registrationRepository::delete);
    }

    /** 某活动报名人数 */
    public long countRegistrations(Long activityId) {
        return registrationRepository.countByActivityId(activityId);
    }

    @Transactional
    public Activity create(Activity activity) {
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity update(Long id, Activity source) {
        Activity target = getById(id);
        if (source.getTitle() != null) target.setTitle(source.getTitle());
        if (source.getRegion() != null) target.setRegion(source.getRegion());
        if (source.getContent() != null) target.setContent(source.getContent());
        if (source.getLocation() != null) target.setLocation(source.getLocation());
        if (source.getStartTime() != null) target.setStartTime(source.getStartTime());
        if (source.getEndTime() != null) target.setEndTime(source.getEndTime());
        if (source.getCoverImage() != null) target.setCoverImage(source.getCoverImage());
        if (source.getCreateTime() != null) target.setCreateTime(source.getCreateTime());
        return activityRepository.save(target);
    }

    @Transactional
    public void delete(Long id) {
        if (!activityRepository.existsById(id)) throw new RuntimeException("活动不存在");
        activityRepository.deleteById(id);
    }
}
