package com.example.demo.repository;

import com.example.demo.entity.UserFavoriteProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFavoriteProjectRepository extends JpaRepository<UserFavoriteProject, Long> {

    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    List<UserFavoriteProject> findByUserIdOrderByCreateTimeDesc(Long userId);
}

