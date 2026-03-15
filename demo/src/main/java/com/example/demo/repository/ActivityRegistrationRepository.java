package com.example.demo.repository;

import com.example.demo.entity.ActivityRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 活动报名记录数据访问层
 */
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long> {

    /** 检查用户是否已报名该活动（防重复报名） */
    Optional<ActivityRegistration> findByUserIdAndActivityId(Long userId, Long activityId);

    /** 某活动的报名人数 */
    long countByActivityId(Long activityId);

    /** 某用户报名的所有活动 ID */
    List<ActivityRegistration> findByUserId(Long userId);
}
