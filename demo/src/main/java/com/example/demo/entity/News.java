package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 非遗资讯实体
 * 发布非遗相关新闻、活动资讯，如各地非遗活动、纪录片推广等
 */
@Entity
@Table(name = "ich_news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    /** 所属区县：丽水市所辖区县之一 */
    @Column(length = 50)
    private String region;

    /** 资讯正文 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 封面图 URL */
    @Column(length = 500)
    private String coverImage;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 来源，如：丽水市非遗保护中心 */
    @Column(length = 100)
    private String source;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (publishTime == null) {
            publishTime = createTime;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
