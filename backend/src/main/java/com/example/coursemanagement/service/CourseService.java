package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Course;

import java.util.List;
import java.util.Map;

/**
 * 课程Service接口
 */
public interface CourseService {

    /**
     * 查询所有课程
     */
    List<Course> list();

    /**
     * 根据ID查询课程
     */
    Course getById(Integer id);

    /**
     * 新增课程
     */
    boolean save(Course course);

    /**
     * 更新课程
     */
    boolean updateById(Course course);

    /**
     * 删除课程
     */
    boolean removeById(Integer id);

    /**
     * 批量删除课程
     */
    boolean removeByIds(List<Integer> ids);

    /**
     * 分页查询课程
     */
    List<Course> listPage(Integer page, Integer limit);

    /**
     * 查询课程总数
     */
    int count();
    
    /**
     * 保存课程学期关联
     */
    boolean saveCourseSemester(Integer courseId, List<Integer> semesterIds);
    
    /**
     * 根据课程ID查询关联的学期ID列表
     */
    List<Integer> getSemesterIdsByCourseId(Integer courseId);
    
    /**
     * 根据学期ID查询课程列表
     */
    List<Course> getCoursesBySemesterId(Integer semesterId);
    
    /**
     * 根据培养方案ID查询课程列表
     */
    List<Course> getCoursesByProgramId(Integer programId);
    
    /**
     * 根据专业ID查询课程列表
     */
    List<Course> getCoursesByMajorId(Integer majorId);
    
    /**
     * 根据专业ID和学期ID查询课程列表
     */
    List<Course> getCoursesByMajorAndSemester(Integer majorId, Integer semesterId);
    
    /**
     * 根据培养方案ID查询四年八个学期的完整课程安排
     */
    Map<Integer, List<Course>> getFullCourseScheduleByProgramId(Integer programId);
    
    /**
     * 获取课程属性统计信息
     */
    Map<String, Object> getCourseStatistics(Integer programId);
}
