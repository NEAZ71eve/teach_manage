package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.KnowledgePoint;
import com.example.coursemanagement.repository.KnowledgePointRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识点Service实现类
 */
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<KnowledgePoint> list() {
        return knowledgePointRepository.findAll();
    }

    @Override
    public KnowledgePoint getById(String id) {
        return knowledgePointRepository.findById(id).orElse(null);
    }

    @Override
    public List<KnowledgePoint> listByCourseId(String courseId) {
        return knowledgePointRepository.findByCourseId(courseId);
    }

    @Override
    public List<KnowledgePoint> listByParentId(String parentId) {
        return knowledgePointRepository.findByParentId(parentId);
    }

    @Override
    public boolean save(KnowledgePoint point) {
        return knowledgePointRepository.save(point) > 0;
    }

    @Override
    public boolean updateById(KnowledgePoint point) {
        return knowledgePointRepository.update(point) > 0;
    }

    @Override
    public boolean removeById(String id) {
        // 检查该知识点是否已被题目引用
        List<?> questions = questionRepository.findByPointId(Integer.parseInt(id));
        if (!questions.isEmpty()) {
            throw new IllegalArgumentException("该知识点已被题目引用，不可删除");
        }
        return knowledgePointRepository.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(List<String> ids) {
        // 检查所有知识点是否已被题目引用
        for (String id : ids) {
            List<?> questions = questionRepository.findByPointId(Integer.parseInt(id));
            if (!questions.isEmpty()) {
                throw new IllegalArgumentException("部分知识点已被题目引用，不可批量删除");
            }
        }
        return knowledgePointRepository.deleteBatch(ids) > 0;
    }

    @Override
    public List<KnowledgePoint> listPage(Integer page, Integer limit) {
        return knowledgePointRepository.findByPage(page, limit);
    }

    @Override
    public int count() {
        return knowledgePointRepository.count();
    }
}
