package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目Controller
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 查询所有题目
     */
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.list());
    }

    /**
     * 分页查询题目
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getQuestionPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<Question> questions = questionService.listPage(page, limit);
        int total = questionService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", questions);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询题目
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    /**
     * 根据课程ID查询题目
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Question>> getQuestionsByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(questionService.listByCourseId(courseId));
    }

    /**
     * 根据知识点ID查询题目
     */
    @GetMapping("/point/{pointId}")
    public ResponseEntity<List<Question>> getQuestionsByPointId(@PathVariable String pointId) {
        return ResponseEntity.ok(questionService.listByPointId(pointId));
    }

    /**
     * 根据题型查询题目
     */
    @GetMapping("/type/{questionType}")
    public ResponseEntity<List<Question>> getQuestionsByType(@PathVariable String questionType) {
        return ResponseEntity.ok(questionService.listByType(questionType));
    }

    /**
     * 根据难度查询题目
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(questionService.listByDifficulty(difficulty));
    }

    /**
     * 新增题目
     */
    @PostMapping
    public ResponseEntity<Boolean> addQuestion(@RequestBody Question question) {
        return ResponseEntity.ok(questionService.save(question));
    }

    /**
     * 更新题目
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        question.setQuestionId(id);
        return ResponseEntity.ok(questionService.updateById(question));
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteQuestion(@PathVariable Integer id) {
        return ResponseEntity.ok(questionService.removeById(id));
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(questionService.removeByIds(ids));
    }
}
