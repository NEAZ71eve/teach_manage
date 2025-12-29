package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.CourseService;
import com.example.coursemanagement.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程Controller
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SemesterService semesterService;

    /**
     * 查询所有课程
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.list());
    }

    /**
     * 分页查询课程
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getCoursePage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<Course> courses = courseService.listPage(page, limit);
        int total = courseService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", courses);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    /**
     * 新增课程
     */
    @PostMapping
    public ResponseEntity<Boolean> addCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.save(course));
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        course.setCourseId(id);
        return ResponseEntity.ok(courseService.updateById(course));
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.removeById(id));
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(courseService.removeByIds(ids));
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String keyword) {
        return ResponseEntity.ok(courseRepository.search(keyword));
    }
    
    /**
     * 保存课程学期关联
     */
    @PostMapping("/{id}/semesters")
    public ResponseEntity<Boolean> saveCourseSemester(@PathVariable Integer id, @RequestBody List<Integer> semesterIds) {
        return ResponseEntity.ok(courseService.saveCourseSemester(id, semesterIds));
    }
    
    /**
     * 获取课程关联的学期ID列表
     */
    @GetMapping("/{id}/semesters")
    public ResponseEntity<List<Integer>> getCourseSemesters(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getSemesterIdsByCourseId(id));
    }
    
    /**
     * 根据学期ID获取课程列表
     */
    @GetMapping("/by-semester/{semesterId}")
    public ResponseEntity<List<Course>> getCoursesBySemester(@PathVariable Integer semesterId) {
        return ResponseEntity.ok(courseService.getCoursesBySemesterId(semesterId));
    }
    
    /**
     * 获取所有学期列表
     */
    @GetMapping("/semesters/all")
    public ResponseEntity<List<com.example.coursemanagement.entity.Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.list());
    }
}
