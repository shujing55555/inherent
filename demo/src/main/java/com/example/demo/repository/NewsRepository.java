package com.example.demo.repository;

import com.example.demo.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 非遗资讯数据访问层
 * 支持关键字(标题/正文/来源)+区县 组合筛选
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAllByOrderByPublishTimeDesc(Pageable pageable);
    Page<News> findByRegionOrderByPublishTimeDesc(String region, Pageable pageable);

    /** 关键词模糊搜索：标题、正文、来源 */
    @Query("SELECT n FROM News n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.source) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 组合条件：关键字(标题/正文/来源)、区县，均可为空 */
    @Query("SELECT n FROM News n WHERE " +
           "(:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(n.source) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:region IS NULL OR :region = '' OR n.region = :region)")
    Page<News> searchByConditions(@Param("keyword") String keyword, @Param("region") String region, Pageable pageable);
}
