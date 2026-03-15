package com.example.demo.service;

import com.example.demo.entity.Inheritor;
import com.example.demo.repository.InheritorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 非遗传承人服务：按姓名排序列表、详情、后台增删改
 */
@Service
public class InheritorService {

    private final InheritorRepository inheritorRepository;

    public InheritorService(InheritorRepository inheritorRepository) {
        this.inheritorRepository = inheritorRepository;
    }

    /** 按姓名排序；可选关键词、区县筛选 */
    public List<Inheritor> listAll(String keyword, String region) {
        List<Inheritor> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = inheritorRepository.searchByKeyword(keyword.trim());
        } else if (region != null && !region.trim().isEmpty()) {
            list = inheritorRepository.findByRegionOrderByNameAsc(region.trim());
        } else {
            list = inheritorRepository.findAllByOrderByNameAsc();
        }
        if (region != null && !region.trim().isEmpty() && (keyword != null && !keyword.trim().isEmpty())) {
            String r = region.trim();
            list = list.stream().filter(i -> r.equals(i.getRegion())).collect(java.util.stream.Collectors.toList());
        }
        return list;
    }

    public Inheritor getById(Long id) {
        return inheritorRepository.findById(id).orElseThrow(() -> new RuntimeException("传承人不存在"));
    }

    @Transactional
    public Inheritor create(Inheritor inheritor) {
        return inheritorRepository.save(inheritor);
    }

    @Transactional
    public Inheritor update(Long id, Inheritor source) {
        Inheritor target = getById(id);
        if (source.getName() != null) target.setName(source.getName());
        if (source.getRegion() != null) target.setRegion(source.getRegion());
        if (source.getIntro() != null) target.setIntro(source.getIntro());
        if (source.getRepresentativeWorks() != null) target.setRepresentativeWorks(source.getRepresentativeWorks());
        if (source.getStory() != null) target.setStory(source.getStory());
        if (source.getAvatar() != null) target.setAvatar(source.getAvatar());
        return inheritorRepository.save(target);
    }

    @Transactional
    public void delete(Long id) {
        if (!inheritorRepository.existsById(id)) throw new RuntimeException("传承人不存在");
        inheritorRepository.deleteById(id);
    }
}
