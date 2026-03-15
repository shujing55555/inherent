package com.example.demo.repository;

import com.example.demo.entity.IchProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 非遗项目数据访问层
 * 支持关键词搜索（标题、描述、分类）、区县、遗产级别组合筛选
 */
public interface IchProjectRepository extends JpaRepository<IchProject, Long> {

    /** 关键词模糊搜索：标题、描述、区县 */
    @Query("SELECT p FROM IchProject p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<IchProject> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 组合条件：关键字(标题/描述/区县)、所属区县、遗产级别、项目类型(ichType)，均可为空 */
    @Query("SELECT p FROM IchProject p WHERE " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:region IS NULL OR :region = '' OR p.category = :region) AND " +
           "(:heritageLevel IS NULL OR :heritageLevel = '' OR p.heritageLevel = :heritageLevel) AND " +
           "(:ichType IS NULL OR :ichType = '' OR p.ichType = :ichType)")
    Page<IchProject> searchByConditions(@Param("keyword") String keyword,
                                        @Param("region") String region,
                                        @Param("heritageLevel") String heritageLevel,
                                        @Param("ichType") String ichType,
                                        Pageable pageable);

    /** 按类型取前若干个项目，按批准时间倒序 */
    java.util.List<IchProject> findTop20ByIchTypeOrderByApprovalTimeDesc(String ichType);

    /** 全局热门项目（按批准时间倒序） */
    java.util.List<IchProject> findTop20ByOrderByApprovalTimeDesc();
}
