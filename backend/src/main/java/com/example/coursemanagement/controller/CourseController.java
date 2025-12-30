package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.CourseService;
import com.example.coursemanagement.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.list());
    }

    /**
     * 分页查询课程
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('course:list')")
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
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    /**
     * 新增课程
     */
    @PostMapping
    @PreAuthorize("hasAuthority('course:add')")
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        try {
            boolean result = courseService.save(course);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("添加课程失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('course:edit')")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        try {
            course.setCourseId(id);
            boolean result = courseService.updateById(course);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新课程失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course:delete')")
    public ResponseEntity<Boolean> deleteCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.removeById(id));
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('course:delete')")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(courseService.removeByIds(ids));
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String keyword) {
        return ResponseEntity.ok(courseRepository.search(keyword));
    }
    
    /**
     * 保存课程学期关联
     */
    @PostMapping("/{id}/semesters")
    @PreAuthorize("hasAuthority('course:edit')")
    public ResponseEntity<Boolean> saveCourseSemester(@PathVariable Integer id, @RequestBody List<Integer> semesterIds) {
        return ResponseEntity.ok(courseService.saveCourseSemester(id, semesterIds));
    }
    
    /**
     * 获取课程关联的学期ID列表
     */
    @GetMapping("/{id}/semesters")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Integer>> getCourseSemesters(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getSemesterIdsByCourseId(id));
    }
    
    /**
     * 根据学期ID获取课程列表
     */
    @GetMapping("/by-semester/{semesterId}")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> getCoursesBySemester(@PathVariable Integer semesterId) {
        return ResponseEntity.ok(courseService.getCoursesBySemesterId(semesterId));
    }
    
    /**
     * 获取所有学期列表
     */
    @GetMapping("/semesters/all")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<com.example.coursemanagement.entity.Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.list());
    }
    
    /**
     * 根据培养方案ID查询课程列表
     */
    @GetMapping("/by-program/{programId}")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> getCoursesByProgram(@PathVariable Integer programId) {
        return ResponseEntity.ok(courseService.getCoursesByProgramId(programId));
    }
    
    /**
     * 根据专业ID查询课程列表
     */
    @GetMapping("/by-major/{majorId}")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> getCoursesByMajor(@PathVariable Integer majorId) {
        return ResponseEntity.ok(courseService.getCoursesByMajorId(majorId));
    }
    
    /**
     * 根据专业ID和学期ID查询课程列表
     */
    @GetMapping("/by-major/{majorId}/semester/{semesterId}")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<List<Course>> getCoursesByMajorAndSemester(@PathVariable Integer majorId, @PathVariable Integer semesterId) {
        return ResponseEntity.ok(courseService.getCoursesByMajorAndSemester(majorId, semesterId));
    }
    
    /**
     * 获取培养方案四年八个学期的完整课程安排
     */
    @GetMapping("/program-full-schedule/{programId}")
    @PreAuthorize("hasAuthority('course:list')")
    public ResponseEntity<Map<Integer, List<Course>>> getProgramFullSchedule(@PathVariable Integer programId) {
        return ResponseEntity.ok(courseService.getFullCourseScheduleByProgramId(programId));
    }
    
    /**
     * 获取课程属性统计信息
     */
    @GetMapping("/statistics/{programId}")
    @PreAuthorize("hasAuthority('statistics:view')")
    public ResponseEntity<Map<String, Object>> getCourseStatistics(@PathVariable Integer programId) {
        return ResponseEntity.ok(courseService.getCourseStatistics(programId));
    }
}
