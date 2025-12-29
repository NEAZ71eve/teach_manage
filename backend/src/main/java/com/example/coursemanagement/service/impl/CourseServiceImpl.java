package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.CourseSemesterRepository;
import com.example.coursemanagement.repository.SemesterRepository;
import com.example.coursemanagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程Service实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseSemesterRepository courseSemesterRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    @Cacheable(value = "course", key = "'all'")
    public List<Course> list() {
        return courseRepository.findAll();
    }

    @Override
    @Cacheable(value = "course", key = "#id")
    public Course getById(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "course", allEntries = true)
    public boolean save(Course course) {
        return courseRepository.save(course) > 0;
    }

    @Override
    @CacheEvict(value = "course", allEntries = true)
    public boolean updateById(Course course) {
        return courseRepository.update(course) > 0;
    }

    @Override
    @CacheEvict(value = "course", allEntries = true)
    public boolean removeById(Integer id) {
        return courseRepository.deleteById(id) > 0;
    }

    @Override
    @CacheEvict(value = "course", allEntries = true)
    public boolean removeByIds(List<Integer> ids) {
        return courseRepository.deleteBatch(ids) > 0;
    }

    @Override
    @Cacheable(value = "course", key = "'page:' + #page + '-limit:' + #limit")
    public List<Course> listPage(Integer page, Integer limit) {
        return courseRepository.findByPage(page, limit);
    }

    @Override
    @Cacheable(value = "course", key = "'count'")
    public int count() {
        return courseRepository.count();
    }
    
    @Override
    @CacheEvict(value = "course", allEntries = true)
    public boolean saveCourseSemester(Integer courseId, List<Integer> semesterIds) {
        try {
            courseSemesterRepository.batchSave(courseId, semesterIds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<Integer> getSemesterIdsByCourseId(Integer courseId) {
        return courseSemesterRepository.findSemesterIdsByCourseId(courseId);
    }
    
    @Override
    public List<Course> getCoursesBySemesterId(Integer semesterId) {
        List<Integer> courseIds = courseSemesterRepository.findCourseIdsBySemesterId(semesterId);
        return courseIds.stream()
                .map(courseId -> courseRepository.findById(courseId).orElse(null))
                .filter(course -> course != null)
                .collect(Collectors.toList());
    }
}
