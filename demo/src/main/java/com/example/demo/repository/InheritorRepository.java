package com.example.demo.repository;

import com.example.demo.entity.Inheritor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 非遗传承人数据访问层
 */
public interface InheritorRepository extends JpaRepository<Inheritor, Long> {

    List<Inheritor> findAllByOrderByNameAsc();
    List<Inheritor> findByRegionOrderByNameAsc(String region);

    /** 关键词模糊搜索：姓名、简介、区县 */
    @Query("SELECT i FROM Inheritor i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.intro) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.region) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Inheritor> searchByKeyword(@Param("keyword") String keyword);
}
