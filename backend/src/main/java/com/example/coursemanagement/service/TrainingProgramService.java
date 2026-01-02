package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.TrainingProgram;

import java.util.List;

/**
 * 培养方案Service接口
 */
public interface TrainingProgramService {

    /**
     * 查询所有培养方案
     */
    List<TrainingProgram> list();

    /**
     * 根据ID查询培养方案
     */
    TrainingProgram getById(Integer id);

    /**
     * 新增培养方案
     */
    boolean save(TrainingProgram program);

    /**
     * 更新培养方案
     */
    boolean updateById(TrainingProgram program);

    /**
     * 删除培养方案
     */
    boolean removeById(Integer id);

    /**
     * 批量删除培养方案
     */
    boolean removeByIds(List<Integer> ids);

    /**
     * 分页查询培养方案
     */
    List<TrainingProgram> listPage(Integer page, Integer limit);

    /**
     * 查询培养方案总数
     */
    int count();
    
    /**
     * 根据老师ID查询培养方案列表
     */
    List<TrainingProgram> getByTeacherId(Integer teacherId);
}
