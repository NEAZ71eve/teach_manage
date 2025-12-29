package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.CourseSchedule;
import com.example.coursemanagement.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程表Controller
 */
@RestController
@RequestMapping("/course-schedule")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * 查询所有课程表
     */
    @GetMapping
    public ResponseEntity<List<CourseSchedule>> getAllCourseSchedules() {
        return ResponseEntity.ok(courseScheduleService.list());
    }

    /**
     * 分页查询课程表
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getCourseSchedulePage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<CourseSchedule> schedules = courseScheduleService.listPage(page, limit);
        int total = courseScheduleService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", schedules);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询课程表
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseSchedule> getCourseScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseScheduleService.getById(id));
    }

    /**
     * 根据学期ID查询课程表
     */
    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<CourseSchedule>> getCourseSchedulesBySemesterId(@PathVariable Integer semesterId) {
        return ResponseEntity.ok(courseScheduleService.getBySemesterId(semesterId));
    }

    /**
     * 根据学期ID和星期几查询周课表
     */
    @GetMapping("/week-schedule")
    public ResponseEntity<List<CourseSchedule>> getWeekSchedule(@RequestParam Integer semesterId, @RequestParam Integer weekDay) {
        return ResponseEntity.ok(courseScheduleService.getWeekSchedule(semesterId, weekDay));
    }

    /**
     * 根据学期ID和班级名称查询周课表
     */
    @GetMapping("/week-schedule/class")
    public ResponseEntity<List<CourseSchedule>> getWeekScheduleByClass(@RequestParam Integer semesterId, @RequestParam String className) {
        return ResponseEntity.ok(courseScheduleService.getWeekScheduleByClass(semesterId, className));
    }

    /**
     * 根据课程ID查询课程表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseSchedule>> getCourseSchedulesByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseScheduleService.getByCourseId(courseId));
    }

    /**
     * 新增课程表
     */
    @PostMapping
    public ResponseEntity<Boolean> addCourseSchedule(@RequestBody CourseSchedule schedule) {
        return ResponseEntity.ok(courseScheduleService.save(schedule));
    }

    /**
     * 更新课程表
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateCourseSchedule(@PathVariable Integer id, @RequestBody CourseSchedule schedule) {
        schedule.setScheduleId(id);
        return ResponseEntity.ok(courseScheduleService.updateById(schedule));
    }

    /**
     * 删除课程表
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCourseSchedule(@PathVariable Integer id) {
        return ResponseEntity.ok(courseScheduleService.removeById(id));
    }

    /**
     * 批量删除课程表
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(courseScheduleService.removeByIds(ids));
    }

    /**
     * 删除指定课程的所有课程表
     */
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Boolean> deleteByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseScheduleService.removeByCourseId(courseId));
    }

    /**
     * 删除指定学期的所有课程表
     */
    @DeleteMapping("/semester/{semesterId}")
    public ResponseEntity<Boolean> deleteBySemesterId(@PathVariable Integer semesterId) {
        return ResponseEntity.ok(courseScheduleService.removeBySemesterId(semesterId));
    }
}