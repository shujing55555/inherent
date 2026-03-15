package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 非遗活动数据访问层
 * 支持关键字(标题/内容/地点)+区县 组合筛选
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findAllByOrderByStartTimeDesc(Pageable pageable);
    Page<Activity> findByRegionOrderByStartTimeDesc(String region, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Activity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 组合条件：关键字(标题/内容/地点)、区县，均可为空 */
    @Query("SELECT a FROM Activity a WHERE " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:region IS NULL OR :region = '' OR a.region = :region)")
    Page<Activity> searchByConditions(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);
}
