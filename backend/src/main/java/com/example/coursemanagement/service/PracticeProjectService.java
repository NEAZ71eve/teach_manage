package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.PracticeProject;

import java.util.List;

/**
 * 实践项目Service接口
 */
public interface PracticeProjectService {

    /**
     * 查询所有实践项目
     */
    List<PracticeProject> list();

    /**
     * 根据ID查询项目
     */
    PracticeProject getById(Integer id);

    /**
     * 新增项目
     */
    boolean save(PracticeProject project);

    /**
     * 更新项目
     */
    boolean updateById(PracticeProject project);

    /**
     * 删除项目
     */
    boolean removeById(Integer id);
}