package com.example.demo.repository;

import com.example.demo.entity.RecommendationWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationWeightRepository extends JpaRepository<RecommendationWeight, Long> {

    Optional<RecommendationWeight> findByIchType(String ichType);
}

