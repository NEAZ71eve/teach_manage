package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.KnowledgePoint;
import com.example.coursemanagement.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<List<KnowledgePoint>> getAllKnowledgePoints() {
        return ResponseEntity.ok(knowledgePointService.list());
    }

    /**
     * 分页查询知识点
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('knowledge-point:list')")
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
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<KnowledgePoint> getKnowledgePointById(@PathVariable Integer id) {
        return ResponseEntity.ok(knowledgePointService.getById(id));
    }

    /**
     * 根据课程ID查询知识点
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<List<KnowledgePoint>> getKnowledgePointsByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(knowledgePointService.listByCourseId(courseId));
    }

    /**
     * 根据父ID查询知识点
     */
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<List<KnowledgePoint>> getKnowledgePointsByParentId(@PathVariable Integer parentId) {
        return ResponseEntity.ok(knowledgePointService.listByParentId(parentId));
    }

    /**
     * 根据课程ID查询知识点树结构
     */
    @GetMapping("/tree/{courseId}")
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<List<KnowledgePoint>> getKnowledgePointTree(@PathVariable Integer courseId) {
        return ResponseEntity.ok(knowledgePointService.getKnowledgePointTree(courseId));
    }

    /**
     * 查询所有知识点树结构
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('knowledge-point:list')")
    public ResponseEntity<List<KnowledgePoint>> getAllKnowledgePointTree() {
        return ResponseEntity.ok(knowledgePointService.getAllKnowledgePointTree());
    }

    /**
     * 新增知识点
     */
    @PostMapping
    @PreAuthorize("hasAuthority('knowledge-point:add')")
    public ResponseEntity<Boolean> addKnowledgePoint(@RequestBody KnowledgePoint point) {
        return ResponseEntity.ok(knowledgePointService.save(point));
    }

    /**
     * 更新知识点
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('knowledge-point:edit')")
    public ResponseEntity<Boolean> updateKnowledgePoint(@PathVariable Integer id, @RequestBody KnowledgePoint point) {
        point.setKpId(id);
        return ResponseEntity.ok(knowledgePointService.updateById(point));
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('knowledge-point:delete')")
    public ResponseEntity<Boolean> deleteKnowledgePoint(@PathVariable Integer id) {
        return ResponseEntity.ok(knowledgePointService.removeById(id));
    }

    /**
     * 批量删除知识点
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('knowledge-point:delete')")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(knowledgePointService.removeByIds(ids));
    }
}
