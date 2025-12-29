package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Semester;
import com.example.coursemanagement.repository.SemesterRepository;
import com.example.coursemanagement.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学期Service实现类
 */
@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public List<Semester> list() {
        return semesterRepository.findAll();
    }

    @Override
    public Semester getById(Integer id) {
        return semesterRepository.findById(id).orElse(null);
    }

    @Override
    public boolean save(Semester semester) {
        return semesterRepository.save(semester) > 0;
    }

    @Override
    public boolean updateById(Semester semester) {
        return semesterRepository.update(semester) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        return semesterRepository.deleteById(id) > 0;
    }

    @Override
    public int count() {
        return semesterRepository.count();
    }
}