package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.KnowledgePoint;
import com.example.coursemanagement.repository.KnowledgePointRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public KnowledgePoint getById(Integer id) {
        return knowledgePointRepository.findById(id).orElse(null);
    }

    @Override
    public List<KnowledgePoint> listByCourseId(Integer courseId) {
        return knowledgePointRepository.findByCourseId(courseId);
    }

    @Override
    public List<KnowledgePoint> listByParentId(Integer parentId) {
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
    public boolean removeById(Integer id) {
        // 检查该知识点是否已被题目引用
        List<?> questions = questionRepository.findByPointId(id);
        if (!questions.isEmpty()) {
            throw new IllegalArgumentException("该知识点已被题目引用，不可删除");
        }
        return knowledgePointRepository.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(List<Integer> ids) {
        // 检查所有知识点是否已被题目引用
        for (Integer id : ids) {
            List<?> questions = questionRepository.findByPointId(id);
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

    @Override
    public List<KnowledgePoint> getKnowledgePointTree(Integer courseId) {
        // 获取指定课程的所有知识点
        List<KnowledgePoint> points = knowledgePointRepository.findByCourseId(courseId);
        // 转换为树形结构
        return buildKnowledgePointTree(points);
    }

    @Override
    public List<KnowledgePoint> getAllKnowledgePointTree() {
        // 获取所有知识点
        List<KnowledgePoint> points = knowledgePointRepository.findAll();
        // 转换为树形结构
        return buildKnowledgePointTree(points);
    }

    /**
     * 将扁平的知识点列表转换为树形结构
     */
    private List<KnowledgePoint> buildKnowledgePointTree(List<KnowledgePoint> points) {
        // 用于存储知识点ID到知识点对象的映射
        Map<Integer, KnowledgePoint> pointMap = new HashMap<>();
        // 根知识点列表
        List<KnowledgePoint> rootPoints = new ArrayList<>();

        // 第一步：将所有知识点放入map中，并初始化children列表
        for (KnowledgePoint point : points) {
            point.setChildren(new ArrayList<>());
            pointMap.put(point.getPointId(), point);
        }

        // 第二步：构建树形结构
        for (KnowledgePoint point : points) {
            Integer parentId = point.getParentId();
            if (parentId == null) {
                // 根知识点
                rootPoints.add(point);
            } else {
                // 子知识点，添加到父知识点的children列表中
                KnowledgePoint parent = pointMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(point);
                }
            }
        }

        return rootPoints;
    }
}
