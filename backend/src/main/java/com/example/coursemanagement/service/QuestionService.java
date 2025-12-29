package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Question;

import java.util.List;

/**
 * 题目Service接口
 */
public interface QuestionService {

    /**
     * 查询所有题目
     */
    List<Question> list();

    /**
     * 根据ID查询题目
     */
    Question getById(Integer id);

    /**
     * 根据课程ID查询题目
     */
    List<Question> listByCourseId(Integer courseId);

    /**
     * 根据知识点ID查询题目
     */
    List<Question> listByPointId(String pointId);

    /**
     * 根据题型查询题目
     */
    List<Question> listByType(String questionType);

    /**
     * 根据难度查询题目
     */
    List<Question> listByDifficulty(String difficulty);

    /**
     * 新增题目
     */
    boolean save(Question question);

    /**
     * 更新题目
     */
    boolean updateById(Question question);

    /**
     * 删除题目
     */
    boolean removeById(Integer id);

    /**
     * 批量删除题目
     */
    boolean removeByIds(List<Integer> ids);

    /**
     * 分页查询题目
     */
    List<Question> listPage(Integer page, Integer limit);

    /**
     * 查询题目总数
     */
    int count();
}
