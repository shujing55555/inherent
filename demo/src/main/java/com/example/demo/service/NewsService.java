package com.example.demo.service;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 非遗资讯服务：分页列表、详情、后台增删改
 */
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /** 分页列表：关键字(标题/正文/来源)、区县 可组合筛选 */
    public Page<News> list(String keyword, String region, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String k = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String r = (region != null && !region.trim().isEmpty()) ? region.trim() : null;
        return newsRepository.searchByConditions(k, r, pageable);
    }

    public News getById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("资讯不存在"));
    }

    @Transactional
    public News create(News news) {
        return newsRepository.save(news);
    }

    @Transactional
    public News update(Long id, News source) {
        News target = getById(id);
        if (source.getTitle() != null) target.setTitle(source.getTitle());
        if (source.getRegion() != null) target.setRegion(source.getRegion());
        if (source.getContent() != null) target.setContent(source.getContent());
        if (source.getCoverImage() != null) target.setCoverImage(source.getCoverImage());
        if (source.getPublishTime() != null) target.setPublishTime(source.getPublishTime());
        if (source.getSource() != null) target.setSource(source.getSource());
        return newsRepository.save(target);
    }

    @Transactional
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) throw new RuntimeException("资讯不存在");
        newsRepository.deleteById(id);
    }
}
