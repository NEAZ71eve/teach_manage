package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Semester;
import com.example.coursemanagement.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学期Controller
 */
@RestController
@RequestMapping("/semester")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    /**
     * 查询所有学期
     */
    @GetMapping
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.list());
    }

    /**
     * 分页查询学期
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getSemesterPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<Semester> semesters = semesterService.list();
        int total = semesterService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", semesters);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询学期
     */
    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable Integer id) {
        return ResponseEntity.ok(semesterService.getById(id));
    }

    /**
     * 查询当前学期
     */
    @GetMapping("/current")
    public ResponseEntity<Semester> getCurrentSemester() {
        // 暂时返回第一个学期，后续需要实现
        List<Semester> semesters = semesterService.list();
        return semesters.isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(semesters.get(0));
    }

    /**
     * 新增学期
     */
    @PostMapping
    public ResponseEntity<Boolean> addSemester(@RequestBody Semester semester) {
        return ResponseEntity.ok(semesterService.save(semester));
    }

    /**
     * 更新学期
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateSemester(@PathVariable Integer id, @RequestBody Semester semester) {
        semester.setSemesterId(id);
        return ResponseEntity.ok(semesterService.updateById(semester));
    }

    /**
     * 删除学期
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSemester(@PathVariable Integer id) {
        return ResponseEntity.ok(semesterService.removeById(id));
    }

    /**
     * 批量删除学期
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        // 暂时实现单个删除
        for (Integer id : ids) {
            semesterService.removeById(id);
        }
        return ResponseEntity.ok(true);
    }
}