package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 非遗项目实体
 * 集中展示丽水市各类非遗项目，包含文字与图片等信息
 */
@Entity
@Table(name = "ich_project")
public class IchProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 项目名称 */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    /** 分类：丽水市所辖区县（莲都区、龙泉市、青田县等） */
    @Column(length = 100)
    private String category;

    /** 非遗项目类型：民间文学、传统音乐、传统舞蹈等（ich_type） */
    @Column(name = "ich_type", length = 100)
    private String ichType;

    /** 项目描述/介绍，富文本或长文本 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 图片 URL，多张用逗号分隔存储 */
    @Column(columnDefinition = "TEXT")
    private String images;

    /** 批准时间 */
    private LocalDateTime approvalTime;

    /** 遗产级别：国家级、省级、市级、县级 */
    @Column(length = 50)
    private String heritageLevel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIchType() { return ichType; }
    public void setIchType(String ichType) { this.ichType = ichType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) { this.approvalTime = approvalTime; }
    public String getHeritageLevel() { return heritageLevel; }
    public void setHeritageLevel(String heritageLevel) { this.heritageLevel = heritageLevel; }
}
