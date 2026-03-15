package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 非遗传承人实体
 * 详情页包含个人简介、代表作品、传承故事
 */
@Entity
@Table(name = "ich_inheritor")
public class Inheritor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 传承人姓名 */
    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    /** 所属区县：丽水市所辖区县之一 */
    @Column(length = 50)
    private String region;

    /** 个人简介 */
    @Column(columnDefinition = "TEXT")
    private String intro;

    /** 代表作品描述 */
    @Column(columnDefinition = "TEXT")
    private String representativeWorks;

    /** 传承故事（详情页展示） */
    @Column(columnDefinition = "TEXT")
    private String story;

    /** 头像 URL */
    @Column(length = 500)
    private String avatar;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
    public String getRepresentativeWorks() { return representativeWorks; }
    public void setRepresentativeWorks(String representativeWorks) { this.representativeWorks = representativeWorks; }
    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
