package com.example.demo.repository;

import com.example.demo.entity.InheritanceStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 非遗传承故事数据访问层
 * 支持关键字(标题/内容)+区县 组合筛选
 */
public interface InheritanceStoryRepository extends JpaRepository<InheritanceStory, Long> {

    Page<InheritanceStory> findAllByOrderByCreateTimeDesc(Pageable pageable);
    Page<InheritanceStory> findByRegionOrderByCreateTimeDesc(String region, Pageable pageable);

    @Query("SELECT s FROM InheritanceStory s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<InheritanceStory> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 组合条件：关键字(标题/内容)、区县，均可为空 */
    @Query("SELECT s FROM InheritanceStory s WHERE " +
           "(:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:region IS NULL OR :region = '' OR s.region = :region)")
    Page<InheritanceStory> searchByConditions(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);
}
