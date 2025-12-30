package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.PracticeProject;
import com.example.coursemanagement.repository.PracticeProjectRepository;
import com.example.coursemanagement.service.PracticeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实践项目Service实现类
 */
@Service
public class PracticeProjectServiceImpl implements PracticeProjectService {

    @Autowired
    private PracticeProjectRepository practiceProjectRepository;

    @Override
    @Cacheable(value = "practiceProject", key = "'all'")
    public List<PracticeProject> list() {
        return practiceProjectRepository.findAll();
    }

    @Override
    @Cacheable(value = "practiceProject", key = "#id")
    public PracticeProject getById(Integer id) {
        return practiceProjectRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "practiceProject", allEntries = true)
    public boolean save(PracticeProject project) {
        return practiceProjectRepository.save(project) > 0;
    }

    @Override
    @CacheEvict(value = "practiceProject", allEntries = true)
    public boolean updateById(PracticeProject project) {
        return practiceProjectRepository.update(project) > 0;
    }

    @Override
    @CacheEvict(value = "practiceProject", allEntries = true)
    public boolean removeById(Integer id) {
        return practiceProjectRepository.deleteById(id) > 0;
    }
}