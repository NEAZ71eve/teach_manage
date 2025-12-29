package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.KnowledgePoint;
import com.example.coursemanagement.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识点Controller
 */
@RestController
@RequestMapping("/knowledge-point")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;

    /**
     * 查询所有知识点
     */
    @GetMapping
    public ResponseEntity<List<KnowledgePoint>> getAllKnowledgePoints() {
        return ResponseEntity.ok(knowledgePointService.list());
    }

    /**
     * 分页查询知识点
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getKnowledgePointPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<KnowledgePoint> points = knowledgePointService.listPage(page, limit);
        int total = knowledgePointService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", points);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询知识点
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgePoint> getKnowledgePointById(@PathVariable String id) {
        return ResponseEntity.ok(knowledgePointService.getById(id));
    }

    /**
     * 根据课程ID查询知识点
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<KnowledgePoint>> getKnowledgePointsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(knowledgePointService.listByCourseId(courseId));
    }

    /**
     * 根据父ID查询知识点
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<KnowledgePoint>> getKnowledgePointsByParentId(@PathVariable String parentId) {
        return ResponseEntity.ok(knowledgePointService.listByParentId(parentId));
    }

    /**
     * 新增知识点
     */
    @PostMapping
    public ResponseEntity<Boolean> addKnowledgePoint(@RequestBody KnowledgePoint point) {
        return ResponseEntity.ok(knowledgePointService.save(point));
    }

    /**
     * 更新知识点
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateKnowledgePoint(@PathVariable String id, @RequestBody KnowledgePoint point) {
        point.setPointId(id);
        return ResponseEntity.ok(knowledgePointService.updateById(point));
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteKnowledgePoint(@PathVariable String id) {
        return ResponseEntity.ok(knowledgePointService.removeById(id));
    }

    /**
     * 批量删除知识点
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return ResponseEntity.ok(knowledgePointService.removeByIds(ids));
    }
}
