package com.example.demo.service;

import com.example.demo.entity.IchProject;
import com.example.demo.repository.IchProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 非遗项目服务：列表、关键词搜索、详情、后台增删改
 */
@Service
public class IchProjectService {

    private final IchProjectRepository projectRepository;

    public IchProjectService(IchProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /** 分页列表：关键字(标题/描述/区县)、所属区县、遗产级别、项目类型 可组合筛选 */
    public Page<IchProject> list(String keyword, String region, String heritageLevel, String ichType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String k = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String r = (region != null && !region.trim().isEmpty()) ? region.trim() : null;
        String h = (heritageLevel != null && !heritageLevel.trim().isEmpty()) ? heritageLevel.trim() : null;
        String t = (ichType != null && !ichType.trim().isEmpty()) ? ichType.trim() : null;
        return projectRepository.searchByConditions(k, r, h, t, pageable);
    }

    public IchProject getById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("非遗项目不存在"));
    }

    @Transactional
    public IchProject create(IchProject project) {
        return projectRepository.save(project);
    }

    @Transactional
    public IchProject update(Long id, IchProject source) {
        IchProject target = getById(id);
        if (source.getTitle() != null) target.setTitle(source.getTitle());
        if (source.getCategory() != null) target.setCategory(source.getCategory());
        if (source.getIchType() != null) target.setIchType(source.getIchType());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getImages() != null) target.setImages(source.getImages());
        target.setApprovalTime(source.getApprovalTime());
        if (source.getHeritageLevel() != null) target.setHeritageLevel(source.getHeritageLevel());
        return projectRepository.save(target);
    }

    @Transactional
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) throw new RuntimeException("非遗项目不存在");
        projectRepository.deleteById(id);
    }
}
