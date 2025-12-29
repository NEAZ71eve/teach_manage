package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Semester;

import java.util.List;

/**
 * 学期Service接口
 */
public interface SemesterService {

    /**
     * 查询所有学期
     */
    List<Semester> list();

    /**
     * 根据ID查询学期
     */
    Semester getById(Integer id);

    /**
     * 新增学期
     */
    boolean save(Semester semester);

    /**
     * 更新学期
     */
    boolean updateById(Semester semester);

    /**
     * 删除学期
     */
    boolean removeById(Integer id);

    /**
     * 查询学期总数
     */
    int count();
}