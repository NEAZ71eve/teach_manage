package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.CourseSchedule;
import com.example.coursemanagement.repository.CourseScheduleRepository;
import com.example.coursemanagement.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程表Service实现类
 */
@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Override
    public List<CourseSchedule> list() {
        return courseScheduleRepository.findAll();
    }

    @Override
    public CourseSchedule getById(Integer id) {
        return courseScheduleRepository.findById(id).orElse(null);
    }

    @Override
    public List<CourseSchedule> getBySemesterId(Integer semesterId) {
        return courseScheduleRepository.findBySemesterId(semesterId);
    }

    @Override
    public List<CourseSchedule> getWeekSchedule(Integer semesterId, Integer weekDay) {
        return courseScheduleRepository.findWeekSchedule(semesterId, weekDay);
    }

    @Override
    public List<CourseSchedule> getByCourseId(Integer courseId) {
        return courseScheduleRepository.findByCourseId(courseId);
    }

    @Override
    public List<CourseSchedule> getWeekScheduleByClass(Integer semesterId, String className) {
        return courseScheduleRepository.findWeekScheduleByClass(semesterId, className);
    }

    @Override
    public boolean save(CourseSchedule schedule) {
        return courseScheduleRepository.save(schedule) > 0;
    }

    @Override
    public boolean updateById(CourseSchedule schedule) {
        return courseScheduleRepository.update(schedule) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        return courseScheduleRepository.deleteById(id) > 0;
    }

    @Override
    public boolean removeByIds(List<Integer> ids) {
        return courseScheduleRepository.deleteBatch(ids) > 0;
    }

    @Override
    public boolean removeByCourseId(Integer courseId) {
        return courseScheduleRepository.deleteByCourseId(courseId) > 0;
    }

    @Override
    public boolean removeBySemesterId(Integer semesterId) {
        return courseScheduleRepository.deleteBySemesterId(semesterId) > 0;
    }

    @Override
    public List<CourseSchedule> listPage(Integer page, Integer limit) {
        return courseScheduleRepository.findByPage(page, limit);
    }

    @Override
    public int count() {
        return courseScheduleRepository.count();
    }
}