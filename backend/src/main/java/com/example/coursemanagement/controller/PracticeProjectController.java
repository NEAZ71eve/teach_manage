package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.PracticeProject;
import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.service.PracticeProjectService;
import com.example.coursemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实践项目Controller
 */
@RestController
@RequestMapping("/practice-project")
public class PracticeProjectController {

    @Autowired
    private PracticeProjectService practiceProjectService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有实践项目
     */
    @GetMapping
    public ResponseEntity<List<PracticeProject>> listAll() {
        return ResponseEntity.ok(practiceProjectService.list());
    }

    /**
     * 获取可分配的普通教师列表
     */
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> listTeachers(@RequestParam(required = false) Integer programId) {
        List<User> users = userService.findAll();
        List<User> teachers = users.stream()
                .filter(user -> "教师".equals(user.getRoleName()))
                .filter(user -> programId == null || programId.equals(user.getProgramId()))
                .peek(user -> user.setPassword(null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(teachers);
    }


    /**
     * 根据ID获取实践项目
     */
    @GetMapping("/{id}")
    public ResponseEntity<PracticeProject> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(practiceProjectService.getById(id));
    }

    /**
     * 新增实践项目
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody PracticeProject project) {
        try {
            boolean result = practiceProjectService.save(project);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("添加实践项目失败: " + e.getMessage());
        }
    }

    /**
     * 修改实践项目
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody PracticeProject project) {
        try {
            project.setId(id);
            boolean result = practiceProjectService.updateById(project);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新实践项目失败: " + e.getMessage());
        }
    }

    /**
     * 删除实践项目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(practiceProjectService.removeById(id));
    }
}
