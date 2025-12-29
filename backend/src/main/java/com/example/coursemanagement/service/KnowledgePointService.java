package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.KnowledgePoint;

import java.util.List;

/**
 * 知识点Service接口
 */
public interface KnowledgePointService {

    /**
     * 查询所有知识点
     */
    List<KnowledgePoint> list();

    /**
     * 根据ID查询知识点
     */
    KnowledgePoint getById(String id);

    /**
     * 根据课程ID查询知识点
     */
    List<KnowledgePoint> listByCourseId(String courseId);

    /**
     * 根据父ID查询子知识点
     */
    List<KnowledgePoint> listByParentId(String parentId);

    /**
     * 新增知识点
     */
    boolean save(KnowledgePoint point);

    /**
     * 更新知识点
     */
    boolean updateById(KnowledgePoint point);

    /**
     * 删除知识点
     */
    boolean removeById(String id);

    /**
     * 批量删除知识点
     */
    boolean removeByIds(List<String> ids);

    /**
     * 分页查询知识点
     */
    List<KnowledgePoint> listPage(Integer page, Integer limit);

    /**
     * 查询知识点总数
     */
    int count();
}
