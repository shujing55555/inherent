package com.example.demo.service;

import com.example.demo.entity.InheritanceStory;
import com.example.demo.repository.InheritanceStoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 非遗传承故事服务：分页列表、详情、后台增删改
 */
@Service
public class InheritanceStoryService {

    private final InheritanceStoryRepository storyRepository;

    public InheritanceStoryService(InheritanceStoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    /** 分页列表：关键字(标题/内容)、区县 可组合筛选 */
    public Page<InheritanceStory> list(String keyword, String region, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String k = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String r = (region != null && !region.trim().isEmpty()) ? region.trim() : null;
        return storyRepository.searchByConditions(k, r, pageable);
    }

    public InheritanceStory getById(Long id) {
        return storyRepository.findById(id).orElseThrow(() -> new RuntimeException("传承故事不存在"));
    }

    @Transactional
    public InheritanceStory create(InheritanceStory story) {
        return storyRepository.save(story);
    }

    @Transactional
    public InheritanceStory update(Long id, InheritanceStory source) {
        InheritanceStory target = getById(id);
        if (source.getTitle() != null) target.setTitle(source.getTitle());
        if (source.getRegion() != null) target.setRegion(source.getRegion());
        if (source.getContent() != null) target.setContent(source.getContent());
        if (source.getCoverImage() != null) target.setCoverImage(source.getCoverImage());
        return storyRepository.save(target);
    }

    @Transactional
    public void delete(Long id) {
        if (!storyRepository.existsById(id)) throw new RuntimeException("传承故事不存在");
        storyRepository.deleteById(id);
    }
}
