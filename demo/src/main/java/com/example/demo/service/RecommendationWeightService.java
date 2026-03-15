package com.example.demo.service;

import com.example.demo.entity.RecommendationWeight;
import com.example.demo.repository.RecommendationWeightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐权重配置服务：读取 / 保存后台配置的权重
 */
@Service
public class RecommendationWeightService {

    private final RecommendationWeightRepository repository;

    public RecommendationWeightService(RecommendationWeightRepository repository) {
        this.repository = repository;
    }

    /** 所有权重配置，直接来自数据库 */
    @Transactional(readOnly = true)
    public List<RecommendationWeight> listAllOrDefault() {
        return repository.findAll();
    }

    /** 保存权重配置（整体覆盖式保存） */
    @Transactional
    public void saveAll(List<RecommendationWeight> weights) {
        // 简化为覆盖式保存：先清空，再保存前端传来的配置
        repository.deleteAll();
        List<RecommendationWeight> toSave = new ArrayList<>();
        for (RecommendationWeight w : weights) {
            if (w.getIchType() == null || w.getIchType().trim().isEmpty()) {
                continue;
            }
            w.setIchType(w.getIchType().trim());
            toSave.add(w);
        }
        repository.saveAll(toSave);
    }

    private RecommendationWeight build(String type, double base, double view, double fav) {
        RecommendationWeight w = new RecommendationWeight();
        w.setIchType(type);
        w.setBaseWeight(base);
        w.setViewWeight(view);
        w.setFavoriteWeight(fav);
        return w;
    }
}

