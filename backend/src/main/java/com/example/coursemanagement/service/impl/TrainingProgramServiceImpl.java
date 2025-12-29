package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.TrainingProgram;
import com.example.coursemanagement.repository.TrainingProgramRepository;
import com.example.coursemanagement.service.TrainingProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 培养方案Service实现类
 */
@Service
public class TrainingProgramServiceImpl implements TrainingProgramService {

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Override
    @Cacheable(value = "trainingProgram", key = "'all'")
    public List<TrainingProgram> list() {
        return trainingProgramRepository.findAll();
    }

    @Override
    @Cacheable(value = "trainingProgram", key = "#id")
    public TrainingProgram getById(Integer id) {
        return trainingProgramRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "trainingProgram", allEntries = true)
    public boolean save(TrainingProgram program) {
        return trainingProgramRepository.save(program) > 0;
    }

    @Override
    @CacheEvict(value = "trainingProgram", allEntries = true)
    public boolean updateById(TrainingProgram program) {
        return trainingProgramRepository.update(program) > 0;
    }

    @Override
    @CacheEvict(value = "trainingProgram", allEntries = true)
    public boolean removeById(Integer id) {
        return trainingProgramRepository.deleteById(id) > 0;
    }

    @Override
    @CacheEvict(value = "trainingProgram", allEntries = true)
    public boolean removeByIds(List<Integer> ids) {
        return trainingProgramRepository.deleteBatch(ids) > 0;
    }

    @Override
    @Cacheable(value = "trainingProgram", key = "'page:' + #page + '-limit:' + #limit")
    public List<TrainingProgram> listPage(Integer page, Integer limit) {
        return trainingProgramRepository.findByPage(page, limit);
    }

    @Override
    @Cacheable(value = "trainingProgram", key = "'count'")
    public int count() {
        return trainingProgramRepository.count();
    }
}
