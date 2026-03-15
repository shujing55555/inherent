package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 非遗活动实体
 * 汇总丽水市非遗相关活动，包含时间、地点、内容；用户可报名参与
 */
@Entity
@Table(name = "ich_activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    /** 所属区县：丽水市所辖区县之一 */
    @Column(length = 50)
    private String region;

    /** 活动内容介绍 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 举办地点 */
    @Column(length = 200)
    private String location;

    /** 报名开始时间（start_time） */
    private LocalDateTime startTime;

    /** 报名结束时间（end_time） */
    private LocalDateTime endTime;

    /** 封面图 */
    @Column(length = 500)
    private String coverImage;

    /** 活动进行时间（create_time） */
    @Column
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
