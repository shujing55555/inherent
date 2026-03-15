package com.example.demo.service;

import com.example.demo.entity.IchProject;
import com.example.demo.entity.UserFavoriteProject;
import com.example.demo.repository.IchProjectRepository;
import com.example.demo.repository.UserFavoriteProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏非遗项目服务
 */
@Service
public class UserFavoriteProjectService {

    private final UserFavoriteProjectRepository favoriteRepository;
    private final IchProjectRepository projectRepository;

    public UserFavoriteProjectService(UserFavoriteProjectRepository favoriteRepository,
                                      IchProjectRepository projectRepository) {
        this.favoriteRepository = favoriteRepository;
        this.projectRepository = projectRepository;
    }

    /** 添加收藏（已存在则忽略） */
    @Transactional
    public void addFavorite(Long userId, Long projectId) {
        if (favoriteRepository.existsByUserIdAndProjectId(userId, projectId)) {
            return;
        }
        UserFavoriteProject fav = new UserFavoriteProject();
        fav.setUserId(userId);
        fav.setProjectId(projectId);
        favoriteRepository.save(fav);
    }

    /** 取消收藏（不存在则忽略） */
    @Transactional
    public void removeFavorite(Long userId, Long projectId) {
        favoriteRepository.deleteByUserIdAndProjectId(userId, projectId);
    }

    /** 当前用户收藏的非遗项目列表，按收藏时间倒序 */
    @Transactional(readOnly = true)
    public List<IchProject> listFavorites(Long userId) {
        List<UserFavoriteProject> favs = favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
        List<Long> ids = favs.stream().map(UserFavoriteProject::getProjectId).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return List.of();
        }
        List<IchProject> projects = projectRepository.findAllById(ids);
        // 按收藏时间顺序排序
        return favs.stream()
                .map(f -> projects.stream()
                        .filter(p -> p.getId().equals(f.getProjectId()))
                        .findFirst()
                        .orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    /** 当前用户是否已收藏指定项目 */
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long projectId) {
        return favoriteRepository.existsByUserIdAndProjectId(userId, projectId);
    }
}

