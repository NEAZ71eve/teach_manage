package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.CourseSchedule;

import java.util.List;

/**
 * 课程表Service接口
 */
public interface CourseScheduleService {

    /**
     * 查询所有课程表
     */
    List<CourseSchedule> list();

    /**
     * 根据ID查询课程表
     */
    CourseSchedule getById(Integer id);

    /**
     * 根据学期ID查询课程表
     */
    List<CourseSchedule> getBySemesterId(Integer semesterId);

    /**
     * 根据学期ID和星期几查询课程表（周课表）
     */
    List<CourseSchedule> getWeekSchedule(Integer semesterId, Integer weekDay);

    /**
     * 根据课程ID查询课程表
     */
    List<CourseSchedule> getByCourseId(Integer courseId);

    /**
     * 根据学期ID和班级名称查询周课表
     */
    List<CourseSchedule> getWeekScheduleByClass(Integer semesterId, String className);

    /**
     * 新增课程表
     */
    boolean save(CourseSchedule schedule);

    /**
     * 更新课程表
     */
    boolean updateById(CourseSchedule schedule);

    /**
     * 删除课程表
     */
    boolean removeById(Integer id);

    /**
     * 批量删除课程表
     */
    boolean removeByIds(List<Integer> ids);

    /**
     * 删除指定课程的所有课程表
     */
    boolean removeByCourseId(Integer courseId);

    /**
     * 删除指定学期的所有课程表
     */
    boolean removeBySemesterId(Integer semesterId);

    /**
     * 分页查询课程表
     */
    List<CourseSchedule> listPage(Integer page, Integer limit);

    /**
     * 查询课程表总数
     */
    int count();
}