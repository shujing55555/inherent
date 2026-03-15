package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 推荐权重配置：按非遗项目类型配置基础权重、浏览权重、收藏权重
 */
@Entity
@Table(name = "recommendation_weight")
public class RecommendationWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 非遗项目类型（ich_type） */
    @Column(nullable = false, length = 100, unique = true)
    private String ichType;

    /** 初始基础权重 */
    @Column(nullable = false)
    private double baseWeight;

    /** 每次浏览的权重增量 */
    @Column(nullable = false)
    private double viewWeight;

    /** 每次收藏的权重增量 */
    @Column(nullable = false)
    private double favoriteWeight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIchType() {
        return ichType;
    }

    public void setIchType(String ichType) {
        this.ichType = ichType;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    public double getViewWeight() {
        return viewWeight;
    }

    public void setViewWeight(double viewWeight) {
        this.viewWeight = viewWeight;
    }

    public double getFavoriteWeight() {
        return favoriteWeight;
    }

    public void setFavoriteWeight(double favoriteWeight) {
        this.favoriteWeight = favoriteWeight;
    }

}

